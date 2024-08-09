package pe.puyu.pukahttp.infrastructure.javalin.server;

import io.javalin.Javalin;
import pe.puyu.pukahttp.infrastructure.javalin.controllers.PrintJobController;
import pe.puyu.pukahttp.infrastructure.javalin.injection.JavalinDependencyInjection;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    public static void config(Javalin app) {
        var printController = JavalinDependencyInjection.loadController(PrintJobController.class);
        app.routes(() -> {

            //TODO: Remove deprecated routes in the future
            path("printer", () -> {
                path("ticket", () -> {
                    post(printController::printTickets);

                });
                path("report", () -> {
                    post(printController::printReport);
                });
            });
        });
    }
}
