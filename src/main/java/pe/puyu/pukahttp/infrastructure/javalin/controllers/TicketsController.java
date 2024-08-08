package pe.puyu.pukahttp.infrastructure.javalin.controllers;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.GatewayTimeoutResponse;
import pe.puyu.pukahttp.application.services.tickets.PrintJobException;
import pe.puyu.pukahttp.application.services.tickets.TicketsService;

public class TicketsController {
    // report
    // print
    // reprint
    // delete
    private final TicketsService ticketsService;

    public TicketsController(TicketsService ticketsService) {
        this.ticketsService = ticketsService;
    }

    public void printTickets(Context ctx) {
        ctx.async(
            15000,
            () -> {
                throw new GatewayTimeoutResponse("print job exceeded 15 seconds");
            },
            () -> {
                try {
                    this.ticketsService.printTickets(ctx.body());
                } catch (PrintJobException e) {
                    throw new BadRequestResponse(e.getMessage());
                }
            }
        );
    }

    public void printReport(Context ctx) {
        ctx.async(
            15000,
            () -> {
                throw new GatewayTimeoutResponse("print job exceeded 15 seconds");
            },
            () -> {
                try {
                    ticketsService.printReport(ctx.body());
                }catch (PrintJobException e){
                    throw new BadRequestResponse(e.getMessage());
                }
            }
        );
    }

}
