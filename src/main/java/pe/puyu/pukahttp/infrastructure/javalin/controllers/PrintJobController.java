package pe.puyu.pukahttp.infrastructure.javalin.controllers;

import com.google.gson.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import pe.puyu.pukahttp.application.services.printjob.PrintServiceNotFoundException;
import pe.puyu.pukahttp.domain.DataValidationException;
import pe.puyu.pukahttp.domain.models.*;
import pe.puyu.pukahttp.infrastructure.javalin.models.DeprecateErrorResponse;
import pe.puyu.pukahttp.infrastructure.javalin.models.DeprecateResponse;
import pe.puyu.pukahttp.infrastructure.javalin.models.PrintJobError;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.domain.PrintQueueObservable;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PrintJobController {
    private final PrintJobService printJobService;
    private final PrintQueueObservable printQueueObservable;
    private final AppLog log = new AppLog(PrintJobController.class);

    public PrintJobController(PrintJobService printJobService, PrintQueueObservable printQueueObservable) {
        this.printJobService = printJobService;
        this.printQueueObservable = printQueueObservable;
    }

    public void printTickets(Context ctx) {
        ctx.async(
            () -> {
                try {
                    log.getLogger().trace("Ticket to print {}", ctx.body());
                    this.printJobService.printTickets(ctx.body());
                    ctx.json(new DeprecateResponse("success", "Trabajo de impresión no lanzo ningun error", 0));
                } catch (PrintJobException e) {
                    log.getLogger().error(e.getMessage());
                    log.getLogger().trace("", e);
                    ctx.json(new DeprecateErrorResponse("error", "Excepción al imprimir", e.getMessage()));
                }
            }
        );
    }

    public void printReport(Context ctx) {
        ctx.async(
            () -> {
                try {
                    printJobService.printReport(ctx.body());
                    ctx.json(new DeprecateResponse("success", "Trabajo de impresión no lanzo ningun error", 0));
                } catch (PrintJobException e) {
                    log.getLogger().error(e.getMessage());
                    log.getLogger().trace("", e);
                    ctx.json(new DeprecateErrorResponse("error", "Excepción al imprimir", e.getMessage()));
                }
            }
        );
    }

    public void print(Context ctx) {
        ctx.async(
            () -> {
                JsonElement body;
                try {
                    body = JsonParser.parseString(ctx.body());
                } catch (JsonSyntaxException e) {
                    throw new BadRequestResponse("body json syntax error.");
                }
                JsonArray printJobs = new JsonArray();
                if (body.isJsonObject()) {
                    printJobs.add(body.getAsJsonObject());
                } else if (body.isJsonArray()) {
                    printJobs = body.getAsJsonArray();
                } else {
                    throw new BadRequestResponse("body is required like json object or json array.");
                }
                String printJobName = "unnamed";
                List<PrintJobError> errors = new LinkedList<>();
                for (JsonElement printJob : printJobs) {
                    try {
                        if (printJob.isJsonObject()) {
                            JsonObject printJobData = printJob.getAsJsonObject();
                            validateDataPrint(printJobData);
                            if (printJobData.has("name") && printJobData.get("name").isJsonPrimitive()) {
                                printJobName = printJobData.get("name").getAsString();
                            }
                            JsonObject printer = printJobData.getAsJsonObject("printer");
                            int times = 1;
                            PrinterType printerType = PrinterType.SYSTEM;
                            String printerName = printer.get("name").getAsString();
                            if (printJobData.has("times")) {
                                times = printJobData.get("times").getAsInt();
                            }
                            if (printer.has("type")) {
                                printerType = PrinterType.from(printer.get("type").getAsString());
                            }
                            printJobData.remove("printer");
                            printJobData.remove("times");
                            printJobData.remove("name");
                            PrinterInfo printerInfo = new PrinterInfo(printerName, printerType);
                            PrintDocument document = new PrintDocument(printerInfo, printJobData.toString(), times);
                            printJobService.print(document);
                            TimeUnit.MILLISECONDS.sleep(50);
                        }
                    } catch (DataValidationException e) {
                        errors.add(PrintJobError.fromException(printJobName, e, "The document was ignored."));
                    } catch (PrintJobException | PrintServiceNotFoundException e) {
                        errors.add(PrintJobError.fromException(printJobName, e, "The document was saved."));
                    } catch (Exception e) {
                        errors.add(PrintJobError.fromException(printJobName, e, "Unknown error on print, the document was ignored."));
                    }
                }
                ctx.status(200);
                if (!errors.isEmpty()) {
                    ctx.status(206).json(errors);
                }
            }
        );
    }

    public void reprint(Context ctx) {
        ctx.async(printJobService::reprint);
    }

    public void release(Context ctx) {
        ctx.async(printJobService::release);
    }


    public void getQueueSize(Context ctx) {
        ctx.async(() -> ctx.result(String.valueOf(printQueueObservable.getQueueSize())));
    }

    public void getQueueEvents(WsConfig ws) {
        ws.onConnect(ctx -> {
            ctx.session.setIdleTimeout(Duration.ofDays(1));
            String observerId = ctx.sessionId();
            printQueueObservable.addObserver(observerId, ctx::send);
            log.getLogger().info("Add observer {} to queue events websocket.", observerId);
            ws.onClose(closeCtx -> {
                String idToRemove = closeCtx.sessionId();
                printQueueObservable.removeObserver(idToRemove);
                log.getLogger().info("close ws connection, then remove observer {}.", idToRemove);
            });
        });
        ws.onError(ctxError -> {
            if (ctxError.error() != null) {
                log.getLogger().warn("Exception an occurred in websockets queue-events with message: {}", ctxError.error().getMessage());
                log.getLogger().trace("more details into fillInStackTrace: ", ctxError.error().fillInStackTrace());
            }
        });
    }

    public void openDrawer(Context ctx) {
        ctx.async(() -> {
            printJobService.openDrawer(ctx.body());
            ctx.json(new DeprecateResponse("success", "Trabajo de impresión no lanzo ningun error", 0));
        });
    }

    private void validateDataPrint(JsonObject printObject) throws DataValidationException {
        if (!printObject.has("printer") || !printObject.get("printer").isJsonObject()) {
            throw new DataValidationException("'printer' parameter is required like json object");
        }
        JsonObject printer = printObject.getAsJsonObject("printer");
        if (!printer.has("name") || !printer.get("name").isJsonPrimitive()) {
            throw new DataValidationException("'printer.name' parameter is required like string");
        }
        if (printer.has("type")) {
            JsonElement typeElement = printer.get("type");
            if (typeElement.isJsonPrimitive()) {
                String type = typeElement.getAsString();
                if (!PrinterType.isValid(type)) {
                    throw new DataValidationException(
                        String.format(
                            "'type' parameter must be: %s, current: %s",
                            Arrays.toString(PrinterType.values()),
                            type
                        )
                    );
                }
            } else {
                throw new DataValidationException("type parameter must be a primitive type");
            }
        }
        if (printObject.has("times")) {
            JsonElement timesElement = printObject.get("times");
            if (timesElement.isJsonPrimitive()) {
                String times = timesElement.getAsString();
                try {
                    Integer.parseInt(times);
                } catch (Exception ignored) {
                    throw new DataValidationException("'times' parameter must be integer. current: " + times);
                }
            } else {
                throw new DataValidationException("'times' parameter must be a primitive type");
            }
        }
    }

}
