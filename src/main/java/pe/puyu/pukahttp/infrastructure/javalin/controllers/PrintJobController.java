package pe.puyu.pukahttp.infrastructure.javalin.controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import pe.puyu.pukahttp.domain.models.*;
import pe.puyu.pukahttp.infrastructure.javalin.models.DeprecateResponse;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.domain.PrintQueueObservable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

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
                    this.printJobService.printTickets(ctx.body());
                    ctx.json(new DeprecateResponse("success", "Trabajo de impresión no lanzo ningun error", 0));
                } catch (PrintJobException e) {
                    log.getLogger().error(e.getMessage(), e);
                    throw new BadRequestResponse(e.getMessage());
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
                    log.getLogger().error(e.getMessage(), e);
                    throw new BadRequestResponse(e.getMessage());
                }
            }
        );
    }

    public void print(Context ctx) {
        ctx.async(
            () -> {
                validateDataPrint(ctx);
                String printerName = Optional.ofNullable(ctx.queryParam("printer")).orElse("");
                PrinterType printerType = PrinterType.from(ctx.queryParam("type"));
                String timesParam = ctx.queryParam("times");
                Integer times = timesParam != null ? Integer.parseInt(timesParam) : 1;
                PrinterInfo printerInfo = new PrinterInfo(printerName, printerType);
                PrintDocument document = new PrintDocument(printerInfo, ctx.body(), times);
                printJobService.print(document);
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

    public void validateDataPrint(Context ctx) {
        if (ctx.queryParam("printer") == null) {
            throw new BadRequestResponse("'printer' parameter is required");
        }
        if (ctx.queryParam("type") != null) {
            String type = ctx.queryParam("type");
            if (!PrinterType.isValid(type)) {
                throw new BadRequestResponse(
                    String.format(
                        "'type' parameter must be: %s, current: %s",
                        Arrays.toString(PrinterType.values()),
                        type
                    )
                );
            }
        }
        String times = ctx.queryParam("times");
        if (times != null) {
            try {
                Integer.parseInt(times);
            } catch (Exception ignored) {
                throw new BadRequestResponse("'times' parameter must be integer. current: " + times);
            }
        }
        if (ctx.body().trim().isEmpty()) {
            throw new BadRequestResponse("body mustn't be empty");
        }
        try {
            JsonElement body = JsonParser.parseString(ctx.body());
            if (!body.isJsonObject()) {
                throw new BadRequestResponse("body json must be a JSON object");
            }
        } catch (Exception e) {
            throw new BadRequestResponse("body json syntax is incorrect");
        }
    }

}
