package pe.puyu.pukahttp.infrastructure.javalin.controllers;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.GatewayTimeoutResponse;
import io.javalin.websocket.WsConfig;
import pe.puyu.pukahttp.infrastructure.loggin.AppLog;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.domain.models.PrintInfo;
import pe.puyu.pukahttp.domain.models.PrinterInfo;
import pe.puyu.pukahttp.domain.models.PrinterType;
import pe.puyu.pukahttp.domain.PrintQueueObservable;

import java.time.Duration;

public class PrintJobController {
    private final int RESPONSE_TIMEOUT = 20000;
    private final PrintJobService printJobService;
    private final PrintQueueObservable printQueueObservable;
    private final AppLog log = new AppLog(PrintJobController.class);

    public PrintJobController(PrintJobService printJobService, PrintQueueObservable printQueueObservable) {
        this.printJobService = printJobService;
        this.printQueueObservable = printQueueObservable;
    }

    public void printTickets(Context ctx) {
        ctx.async(
            RESPONSE_TIMEOUT,
            () -> {
                throw new GatewayTimeoutResponse("print job exceeded 15 seconds");
            },
            () -> {
                try {
                    this.printJobService.printTickets(ctx.body());
                } catch (PrintJobException e) {
                    log.getLogger().error(e.getMessage(), e);
                    throw new BadRequestResponse(e.getMessage());
                }
            }
        );
    }

    public void printReport(Context ctx) {
        ctx.async(
            RESPONSE_TIMEOUT,
            () -> {
                throw new GatewayTimeoutResponse("print job exceeded 15 seconds");
            },
            () -> {
                try {
                    printJobService.printReport(ctx.body());
                } catch (PrintJobException e) {
                    log.getLogger().error(e.getMessage(), e);
                    throw new BadRequestResponse(e.getMessage());
                }
            }
        );
    }

    public void print(Context ctx) {
        ctx.async(
            RESPONSE_TIMEOUT,
            () -> {
                throw new GatewayTimeoutResponse("print job exceeded 15 seconds");
            },
            () -> {
                String target = ctx.queryParam("printer");
                PrinterType type = PrinterType.from(ctx.queryParam("type"));
                String port = ctx.queryParam("port");
                String times = ctx.queryParam("times");
                String printObject = ctx.body();
                if (target == null) {
                    throw new BadRequestResponse("query parameter 'printer' is required");
                }
                PrinterInfo printerInfo = new PrinterInfo(target, type, port);
                PrintInfo printInfo = new PrintInfo(printerInfo, times, printObject);
                printJobService.print(printInfo);
            }
        );
    }

    public void reprint(Context ctx) {
        ctx.async(
            RESPONSE_TIMEOUT * printQueueObservable.getQueueSize() + 5,
            () -> {
                throw new GatewayTimeoutResponse("print job exceeded 15 seconds");
            },
            printJobService::reprint
        );
    }

    public void release(Context ctx){
        ctx.async(
            RESPONSE_TIMEOUT,
            () -> {
                throw new GatewayTimeoutResponse("print job exceeded 15 seconds");
            },
            printJobService::release
        );
    }


    public void getQueueSize(Context ctx) {
        ctx.async(
            RESPONSE_TIMEOUT,
            () -> {
                throw new GatewayTimeoutResponse("print job exceeded 15 seconds");
            },
            () -> ctx.result(String.valueOf(printQueueObservable.getQueueSize()))
        );
    }

    public void getQueueEvents(WsConfig ws) {
        ws.onConnect(ctx -> {
            ctx.session.setIdleTimeout(Duration.ofDays(1));
            String observerId = ctx.getSessionId();
            printQueueObservable.addObserver(observerId, ctx::send);
            log.getLogger().info("Add observer {} to queue events websocket.", observerId);
            ws.onClose(closeCtx -> {
                String idToRemove = closeCtx.getSessionId();
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

    public void openDrawer(Context ctx){
        ctx.async(
            RESPONSE_TIMEOUT,
            () -> {
                throw new GatewayTimeoutResponse("print job exceeded 15 seconds");
            },
            () -> printJobService.openDrawer(ctx.body())
        );
    }


}
