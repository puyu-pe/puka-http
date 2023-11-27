package pe.puyu.sweetprinterpos.services.api;

import ch.qos.logback.classic.Logger;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.bundled.CorsPluginConfig;
import javafx.application.Platform;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.util.AppUtil;
import pe.puyu.sweetprinterpos.util.FileSystemLock;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PrintServer {
	private final Javalin app;
	private final FileSystemLock lock;
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("PrintServer"));

	public PrintServer() {
		app = Javalin.create(this::serverConfig);
		lock = new FileSystemLock(AppUtil.makeLockFile("lockPrintService"));
	}

	public boolean isRunningInOtherProcess() {
		return lock.hasLock();
	}

	public void listen(String ip, int port) {
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
		}
	}

	private void serverConfig(JavalinConfig config) {
		config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
		config.jsonMapper(new FxGsonMapper());
	}

	private void configRoutes() {
		app.routes(() -> {
				path("printer", () -> {
					path("system", () -> get(PrinterController::getAllPrinterSystem));
					path("ticket", () -> {
						post(PrinterController::printTickets);
					});
				});
				get("test-connection", (ctx) -> ctx.result("service online"));
				post("stop-service", this::stopService);
			}
		);
		app.exception(Exception.class, PrinterController::genericErrorHandling);
		/*
		app.routes(() -> {
			path("printer"-> {
				get(PrinterController::getSavedPrinters)
				path("{name}",() -> {
					get(PrinterController::getPrinter)
					post(PrinterController::savePrinter)
					put(PrinterController::updatePrinter)
					delete(PrinterController::deletePrinter)
					path("queue",() -> {
						get(PrinterController::getSizeQueueByPrinter)
						ws("events", PrinterController::getQueueEventsByPrinter)
					})
				})
				path("system", () -> {
					get(PrinterController::getAllPrintersSystem)
				})
				path("ticket", () -> {
					post(PrinterController::printTickets)
					path("queue", () -> {
						get(PrinterController::getSizeQueue)
						ws("events", PrinterController::getQueueEvents)
					})
				})
				path("test" , () -> {
					get(PrinterController::printerTest)
				})
			})
			path("info", () -> {
				get("server", ...) // informacion sobre la dependencias o estado del servidor, ip , puerto ,etc
				put("debug", ...) // modifica el nivel de depuración
			})
		})
		* */
	}

	private void stopService(Context ctx) {
		var response = new ResponseApi<>();
		response.setStatus("success");
		response.setMessage("Try force stop Service.");
		ctx.json(response);
		new Thread(() -> {
			try {
				Thread.sleep(100);
				lock.unLock();
				app.close();
				logger.info("close service.");
				Platform.exit();
			} catch (Exception e) {
				logger.error("Exception on stopService {}", e.getMessage(), e);
			}
		}).start();
	}

}
