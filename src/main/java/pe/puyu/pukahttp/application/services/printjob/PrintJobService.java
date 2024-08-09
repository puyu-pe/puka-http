package pe.puyu.pukahttp.application.services.printjob;

import pe.puyu.SweetTicketDesign.application.builder.gson.GsonPrinterObjectBuilder;
import pe.puyu.pukahttp.application.services.printjob.deprecated.JTicketDesignPrintJob;
import pe.puyu.pukahttp.domain.PrintData;

public class PrintJobService {

    public void printTickets(String jsonArray) throws PrintJobException {
        JTicketDesignPrintJob.print(jsonArray);
    }

    public void printReport(String jsonObject) throws PrintJobException {
        JTicketDesignPrintJob.report(jsonObject);
    }

    public void print(PrintData data){
        GsonPrinterObjectBuilder jsonBuilder = new GsonPrinterObjectBuilder(data.printObject());
    }

}
