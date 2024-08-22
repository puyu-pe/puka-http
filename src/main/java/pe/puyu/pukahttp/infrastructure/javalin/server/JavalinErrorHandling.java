package pe.puyu.pukahttp.infrastructure.javalin.server;

import io.javalin.http.Context;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.domain.DataValidationException;

public class JavalinErrorHandling {

    public static void generic(Exception e, Context ctx) {
        ctx.status(500);
        AppLog log = new AppLog(JavalinErrorHandling.class);
        log.getLogger().error("Unknown exception", e);
        ctx.json(String.format("Unknown exception: %s", e.getMessage()));
    }

    public static void dataValidationException(DataValidationException e, Context ctx) {
        ctx.status(400);
        ctx.json(e.getMessage());
    }

}
