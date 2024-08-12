package pe.puyu.pukahttp.infrastructure.javalin.server;

import io.javalin.Javalin;
import pe.puyu.pukahttp.infrastructure.javalin.controllers.PrintJobController;
import pe.puyu.pukahttp.infrastructure.javalin.injection.JavalinDependencyInjection;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    public static void config(Javalin app) {
        var printController = JavalinDependencyInjection.loadController(PrintJobController.class);
        app.routes(() -> {
            get(ctx -> ctx.result("Good job, print service online."));
            path("print", () -> {
                post(printController::print);
                path("queue", () -> {
                    put(printController::reprint);
                    delete(printController::release);
                    get(printController::getQueueSize);
                    ws("events", printController::getQueueEvents);
                });
            });
            //TODO: Remove above deprecated routes in the future
            path("printer", () -> {
                path("open-drawer", () -> post(printController::openDrawer));
                path("ticket", () -> {
                    path("reprint", () -> get(printController::reprint));
                    path("queue", () -> {
                        get(printController::getQueueSize);
                        ws("events", printController::getQueueEvents);
                    });
                    post(printController::printTickets);
                    delete(printController::release);
                });
                path("report", () -> post(printController::printReport));
            });
            get("test-connection", (ctx) -> ctx.result("service online"));
        });
    }
}
