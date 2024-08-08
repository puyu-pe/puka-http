package pe.puyu.pukahttp.infrastructure.javalin.controllers;


import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.GatewayTimeoutResponse;
import pe.puyu.pukahttp.application.services.tickets.PrintTicketException;
import pe.puyu.pukahttp.application.services.tickets.TicketsService;


public class TicketsController {
    // ticket
    // reprint
    // print
    // report
    // delete
    private final TicketsService ticketsService;

    public TicketsController(TicketsService ticketsService) {
        this.ticketsService = ticketsService;
    }

    public void printTicket(Context ctx) {
        ctx.async(
            15000,
            () -> {
                throw new GatewayTimeoutResponse("print job exceeded 15 seconds");
            },
            () -> {
                try {
                    this.ticketsService.printTicket(ctx.body());
                } catch (PrintTicketException e) {
                    throw new BadRequestResponse(e.getMessage());
                }
            }
        );
    }

}
