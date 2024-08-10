package pe.puyu.pukahttp.infrastructure.javalin.server;

import io.javalin.Javalin;
import pe.puyu.pukahttp.infrastructure.javalin.controllers.PrintJobController;
import pe.puyu.pukahttp.infrastructure.javalin.injection.JavalinDependencyInjection;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    public static void config(Javalin app) {
        var printController = JavalinDependencyInjection.loadController(PrintJobController.class);
        app.routes(() -> {
            path("printer", () -> {
                path("print", () -> {
                    post(printController::print);
                });
                path("queue", () -> {
                    get(printController::getQueueSize);
                    ws("events", printController::getQueueEvents);
                });
                //TODO: Remove above deprecated routes in the future
                path("ticket", () -> {
                    post(printController::printTickets);
                    path("queue", () -> {
                        get(printController::getQueueSize);
                        ws("events", printController::getQueueEvents);
                    });
                });
                path("report", () -> {
                    post(printController::printReport);
                });
            });
        });
    }
}
