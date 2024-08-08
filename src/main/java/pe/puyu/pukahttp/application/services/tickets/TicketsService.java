package pe.puyu.pukahttp.application.services.tickets;

import pe.puyu.pukahttp.application.services.tickets.deprecated.PrintJob;

public class TicketsService {

    public void printTickets(String jsonArray) throws PrintJobException {
        PrintJob.print(jsonArray);
    }

    public void printReport(String jsonObject) throws PrintJobException {
        PrintJob.report(jsonObject);
    }

}
