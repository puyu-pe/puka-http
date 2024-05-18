package pe.puyu.pukahttp.services.api;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.bundled.CorsPluginConfig;
import javafx.application.Platform;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.Constants;
import pe.puyu.pukahttp.repository.AppDatabase;
import pe.puyu.pukahttp.services.trayicon.TrayIconServiceProvider;
import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.FileSystemLock;
import pe.puyu.pukahttp.util.Notifier;

import java.util.function.BiConsumer;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PrintServer {
	private final Javalin app;
	private final FileSystemLock lock;
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("PrintServer"));
	private final Notifier notifier = new Notifier();
	private PrinterApiController controller;
	private AppDatabase db;

	public PrintServer() {
		notifier.setDefaultTitle("Servicio de impresión");
		app = Javalin.create(this::serverConfig);
		lock = new FileSystemLock(AppUtil.makeLockFile("lockPrintService"));
		if (!isRunningInOtherProcess()) {
			db = new AppDatabase();
			controller = new PrinterApiController(db);
			Runtime.getRuntime().addShutdownHook(new Thread(db::close));
		}
	}

	public boolean isRunningInOtherProcess() {
		return lock.hasLock();
	}

	public void listen(String ip, int port) throws Exception {
		try {
			if (!isRunningInOtherProcess()) {
				configRoutes();
				app.start(ip, port);
				logger.info("Start service on {}:{}", ip, port);
			} else {
				logger.info("An attempt was made to launch server on {}:{}", ip, port);
				logger.info("Service already run in other process");
			}
			notifier.info(String.format("online en %s:%d", ip, port));
		} catch (Exception e) {
			logger.error("Exception on star service on {}:{} -> {}", ip, port, e.getLocalizedMessage());
			throw new Exception(e);
		}
	}

	public void closeService() {
		try {
			lock.unLock();
			app.close();
			db.close();
			logger.info("close service.");
			notifier.info("Se cerro correctamente.");
			notifier.closeNotification();
		} catch (Exception e) {
			logger.error("Exception on stopService {}", e.getMessage(), e);
		}
	}

	public void addListenerInfoNotification(BiConsumer<String, String> listener) {
		notifier.addInfoSubscriber(listener);
	}

	public void addListenerErrorNotification(BiConsumer<String, String> listener) {
		notifier.addErrorSubscriber(listener);
	}

	public void addListenerWarnNotification(BiConsumer<String, String> listener) {
		notifier.addWarnSubscriber(listener);
	}

	private void serverConfig(JavalinConfig config) {
		config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
		config.jsonMapper(new FxGsonMapper());
	}

	private void configRoutes() {
		if (controller == null)
			return;
		app.routes(() -> {
				path("printer", () -> {
					path("system", () -> get(controller::getAllPrinterSystem));
					path("open-drawer", () -> post(ctx -> controller.openDrawer(ctx, notifier)));
					path("ticket", () -> {
						path("reprint", () -> get(ctx -> controller.reprintTickets(ctx, notifier)));
						path("queue", () -> {
							get(controller::getSizeQueue);
							ws("events", controller::getQueueEvents);
						});
						post(ctx -> controller.printTickets(ctx, notifier));
						delete(ctx -> controller.deleteTickets(ctx, notifier));
					});
				});
				get("test-connection", (ctx) -> ctx.result("service online"));
				get("stop-service", this::stopService);
				path("info", () -> {
					put("debug", this::updateLogLevel);
					get("debug", this::getLogLevel);
				});
			}
		);
		app.exception(Exception.class, (e, ctx) -> controller.genericErrorHandling(e, ctx, notifier));
	}

	private void updateLogLevel(Context ctx) {
		var newLevel = JsonParser.parseString(ctx.body()).getAsString();
		Logger rootLogger = (Logger) LoggerFactory.getLogger(Constants.PACKAGE_BASE_PATH);
		rootLogger.setLevel(Level.toLevel(newLevel));
		var response = new ResponseApi<String>();
		response.setStatus("success");
		response.setMessage("Se actualizó el nivel de logs");
		response.setData(rootLogger.getLevel().toString());
		ctx.json(response);
	}

	private void getLogLevel(Context ctx) {
		Logger rootLogger = (Logger) LoggerFactory.getLogger(Constants.PACKAGE_BASE_PATH);
		var response = new ResponseApi<String>();
		response.setStatus("success");
		response.setMessage("Se recupera el nivel de logs del servidor");
		response.setData(rootLogger.getLevel().toString());
		ctx.json(response);
	}

	private void stopService(Context ctx) {
		var response = new ResponseApi<>();
		response.setStatus("success");
		response.setMessage("Try force stop Service.");
		ctx.json(response);
		//Necesario envolver en un hilo, de lo contrario no habra respuesta de la accion en el cliente
		new Thread(() -> {
			this.closeService();
			if (!TrayIconServiceProvider.isLock()) {
				Platform.exit();
			}
		}).start();
	}

}
