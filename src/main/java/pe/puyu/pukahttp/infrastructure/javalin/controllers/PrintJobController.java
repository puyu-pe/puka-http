package pe.puyu.pukahttp.infrastructure.javalin.controllers;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.GatewayTimeoutResponse;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintJobService;
import pe.puyu.pukahttp.domain.PrintData;
import pe.puyu.pukahttp.domain.PrinterType;

public class PrintJobController {
    private final int RESPONSE_TIMEOUT = 20000;
    // print
    // reprint
    // delete
    private final PrintJobService printJobService;

    public PrintJobController(PrintJobService printJobService) {
        this.printJobService = printJobService;
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

                PrintData printData = new PrintData(target, type, port, times, printObject);
                printJobService.print(printData);
            }
        );
    }

}
