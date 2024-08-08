package pe.puyu.pukahttp.infrastructure.javalin.server;

import io.javalin.Javalin;
import pe.puyu.pukahttp.infrastructure.javalin.controllers.TicketsController;
import pe.puyu.pukahttp.infrastructure.javalin.injection.JavalinDependencyInjection;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    public static void config(Javalin app) {
        var printController = JavalinDependencyInjection.loadController(TicketsController.class);
        app.routes(() -> {

            // deprecated routes
            path("printer", () -> {
                path("ticket", () -> {
                    post(printController::printTicket);
                });
            });
        });
    }
}
