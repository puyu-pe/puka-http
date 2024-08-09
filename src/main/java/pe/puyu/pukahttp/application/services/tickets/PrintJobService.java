package pe.puyu.pukahttp.application.services.tickets;

import pe.puyu.pukahttp.application.services.tickets.deprecated.JTicketDesignPrintJob;

public class PrintJobService {

    public void printTickets(String jsonArray) throws PrintJobException {
        JTicketDesignPrintJob.print(jsonArray);
    }

    public void printReport(String jsonObject) throws PrintJobException {
        JTicketDesignPrintJob.report(jsonObject);
    }


}
