package pe.puyu.pukahttp.application.services.tickets;

import pe.puyu.pukahttp.application.services.tickets.deprecated.PrintJob;

public class TicketsService {

    public void printTicket(String jsonArray) throws PrintTicketException {
        PrintJob.print(jsonArray);
    }

}
