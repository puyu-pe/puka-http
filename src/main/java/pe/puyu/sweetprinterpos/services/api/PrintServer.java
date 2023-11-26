package pe.puyu.sweetprinterpos.services.api;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.plugin.bundled.CorsPluginConfig;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PrintServer {
	private final Javalin app;

	public PrintServer() {
		app = Javalin.create(this::serverConfig);
	}

	public void listen(String ip, int port) {
		configRoutes();
		app.start(ip, port);
	}

	private void serverConfig(JavalinConfig config) {
		config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
		config.jsonMapper(new FxGsonMapper());
	}

	private void configRoutes() {
		app.routes(() -> {
				path("printer", () -> {
					path("system", () -> get(PrinterController::getAllPrinterSystem));
					path("ticket",() -> {
						post(PrinterController::printTickets);
					});
				});
				get("test-connection", (ctx) -> ctx.result("service online"));
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

}
