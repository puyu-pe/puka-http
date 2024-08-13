package pe.puyu.pukahttp.infrastructure.javalin.server;

import io.javalin.http.Context;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintServiceNotFoundException;
import pe.puyu.pukahttp.domain.DataValidationException;

public class JavalinErrorHandling {

    public static void generic(Exception e, Context ctx) {
        ctx.status(500);
        ctx.json(String.format("Unknown exception: %s", e.getMessage()));
    }

    public static void printServiceNotFoundExceptionHandler(PrintServiceNotFoundException e, Context ctx) {
        AppLog log = new AppLog(PrintServiceNotFoundException.class);
        log.getLogger().error(e.getMessage(), e);
        ctx.status(404);
        ctx.json(e.getMessage());
    }

    public static void printJobExceptionHandler(PrintJobException e, Context ctx) {
        AppLog log = new AppLog(PrintJobException.class);
        log.getLogger().error(e.getMessage(), e);
        ctx.status(500);
        ctx.json(e.getMessage());
    }

    public static void validationExceptionHandler(DataValidationException e, Context ctx) {
        ctx.status(400);
        ctx.json(e.getMessage());
    }

}
