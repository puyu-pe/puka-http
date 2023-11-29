package pe.puyu.sweetprinterpos.services.api;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.bundled.CorsPluginConfig;
import javafx.application.Platform;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.app.App;
import pe.puyu.sweetprinterpos.repository.AppDatabase;
import pe.puyu.sweetprinterpos.util.AppUtil;
import pe.puyu.sweetprinterpos.util.FileSystemLock;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PrintServer {
	private final Javalin app;
	private final FileSystemLock lock;
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("PrintServer"));
	private PrinterApiController controller;
	private AppDatabase db;

	public PrintServer() {
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
		} catch (Exception e) {
			logger.error("Exception on stopService {}", e.getMessage(), e);
		}
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
					path("ticket", () -> {
						post(controller::printTickets);
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
		app.exception(Exception.class, controller::genericErrorHandling);
		/*
		app.routes(() -> {
			path("printer"-> {
				get(PrinterApiController::getSavedPrinters)
				path("{name}",() -> {
					get(PrinterApiController::getPrinter)
					post(PrinterApiController::savePrinter)
					put(PrinterApiController::updatePrinter)
					delete(PrinterApiController::deletePrinter)
					path("queue",() -> {
						get(PrinterApiController::getSizeQueueByPrinter)
						ws("events", PrinterApiController::getQueueEventsByPrinter)
					})
				})
				path("system", () -> {
					get(PrinterApiController::getAllPrintersSystem)
				})
				path("ticket", () -> {
					post(PrinterApiController::printTickets)
					path("queue", () -> {
						get(PrinterApiController::getSizeQueue)
						ws("events", PrinterApiController::getQueueEvents)
					})
				})
				path("test" , () -> {
					get(PrinterApiController::printerTest)
				})
			})
			path("info", () -> {
				get("server", ...) // informacion sobre la dependencias o estado del servidor, ip , puerto ,etc
				put("debug", ...) // modifica el nivel de depuración
			})
		})
		* */
	}

	private void updateLogLevel(Context ctx) {
		var newLevel = JsonParser.parseString(ctx.body()).getAsString();
		App.rootLogger.setLevel(Level.toLevel(newLevel));
		var response = new ResponseApi<String>();
		response.setStatus("success");
		response.setMessage("Se actualizó el nivel de logs");
		response.setData(App.rootLogger.getLevel().toString());
		ctx.json(response);
	}

	private void getLogLevel(Context ctx) {
		var response = new ResponseApi<String>();
		response.setStatus("success");
		response.setMessage("Se recupera el nivel de logs del servidor");
		response.setData(App.rootLogger.getLevel().toString());
		ctx.json(response);
	}

	private void stopService(Context ctx) {
		var response = new ResponseApi<>();
		response.setStatus("success");
		response.setMessage("Try force stop Service.");
		ctx.json(response);
		//Necesario envolver en un hilo, de lo contrario no habra respuesta de la accion en el cliente
		new Thread(() -> {
			closeService();
			Platform.exit();
		}).start();
	}

}
