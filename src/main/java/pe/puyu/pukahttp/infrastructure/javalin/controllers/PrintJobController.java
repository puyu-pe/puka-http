package pe.puyu.pukahttp.infrastructure.javalin.controllers;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.GatewayTimeoutResponse;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
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
    private final PrintJobService printJobService;
    private final PrintQueueObservable printQueueObservable;
    private final AppLog log = new AppLog(PrintJobController.class);

    public PrintJobController(PrintJobService printJobService, PrintQueueObservable printQueueObservable) {
        this.printJobService = printJobService;
        this.printQueueObservable = printQueueObservable;
    }

    @OpenApi(
        path = "/printer/ticket",
        methods = {HttpMethod.POST},
        summary = "deprecated endpoint: print ticket with JTicketDesign",
        operationId = "printTickets",
        tags = {"Print","Deprecated"}
    )
    public void printTickets(Context ctx) {
        ctx.async(
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

    @OpenApi(
        path = "/printer/report",
        methods = {HttpMethod.POST},
        summary = "deprecated endpoint: print report with JTicketDesign",
        operationId = "printReport",
        tags = {"Print","Deprecated"}
    )
    public void printReport(Context ctx) {
        ctx.async(
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

    @OpenApi(
        path = "/print",
        methods = {HttpMethod.POST},
        summary = "Generic print with SweetTicketDesign specifications",
        operationId = "print",
        tags = {"Print"}
    )
    public void print(Context ctx) {
        ctx.async(
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

    @OpenApi(
        path = "/print/queue",
        methods = {HttpMethod.PUT},
        summary = "Reprint failed print jobs",
        operationId = "reprint",
        tags = {"Print"}
    )
    public void reprint(Context ctx) {
        ctx.async(printJobService::reprint);
    }

    @OpenApi(
        path = "/print/queue",
        methods = {HttpMethod.DELETE},
        summary = "Release failed print jobs",
        operationId = "release",
        tags = {"Print"}
    )
    public void release(Context ctx) {
        ctx.async(printJobService::release);
    }

    @OpenApi(
        path = "/print/queue",
        methods = {HttpMethod.GET},
        summary = "Get queue print jobs",
        operationId = "getQueueSize",
        tags = {"Print"}
    )
    public void getQueueSize(Context ctx) {
        ctx.async(() -> ctx.result(String.valueOf(printQueueObservable.getQueueSize())));
    }

    @OpenApi(
        path = "/print/queue",
        summary = "Connection with websockets for queue size listening",
        operationId = "getQueueEvents",
        tags = {"Print"}
    )
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

    @OpenApi(
        path = "/printer/open-drawer",
        methods = {HttpMethod.POST},
        summary = "Deprecated endpoint for open drawer with JTicketDesign",
        operationId = "openDrawer",
        tags = {"Print","Deprecated"}
    )
    public void openDrawer(Context ctx) {
        ctx.async(() -> printJobService.openDrawer(ctx.body()));
    }


}
