package pe.puyu.pukahttp.infrastructure.javalin.server;

import io.javalin.http.Context;

public class JavalinErrorHandling {

    public static void generic(Exception e, Context ctx){
        ctx.status(500);
        ctx.json(String.format("Unknown exception: %s", e.getMessage()));
    }

}
