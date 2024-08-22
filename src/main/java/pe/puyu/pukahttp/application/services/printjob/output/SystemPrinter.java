package pe.puyu.pukahttp.application.services.printjob.output;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintServiceNotFoundException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.DocFlavor.INPUT_STREAM;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class SystemPrinter extends MyPrinter{
    private PrintService printService = null;

    public SystemPrinter(String printServiceName) {
        super(printServiceName);
    }

    @Override
    public void print(@NotNull ByteArrayOutputStream buffer) throws PrintServiceNotFoundException, PrintJobException {
        makePrintService(service);
        ByteArrayInputStream is = new ByteArrayInputStream(buffer.toByteArray());
        DocFlavor df = INPUT_STREAM.AUTOSENSE;
        Doc doc = new SimpleDoc(is, df, null);
        DocPrintJob printJob = printService.createPrintJob();
        try {
            printJob.print(doc, null);
        } catch (PrintException e) {
            throw new PrintJobException(e.getMessage(), e);
        }
    }

    public static String[] getListPrintServicesNames() {
        PrintService[] printServices = PrinterJob.lookupPrintServices();
        String[] printServicesNames = new String[printServices.length];

        for(int i = 0; i < printServices.length; ++i) {
            printServicesNames[i] = printServices[i].getName();
        }

        return printServicesNames;
    }

    private void makePrintService(String printServiceName) throws PrintServiceNotFoundException {
        if (printService == null) {
            PrintService[] printServices = PrinterJob.lookupPrintServices();
            for (PrintService service : printServices) {
                if (service.getName().compareToIgnoreCase(printServiceName) == 0) {
                    this.printService = service;
                    return;
                }
            }
            if (printService == null) {
                throw new PrintServiceNotFoundException(String.format("System Print name: '%s' was not found.", printServiceName));
            }
        }
    }
}
