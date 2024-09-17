package pe.puyu.pukahttp.application.services.printjob;

import pe.puyu.SweetTicketDesign.domain.components.SweetDefaultComponentsProvider;
import pe.puyu.pukahttp.application.notifier.AppNotifier;
import pe.puyu.pukahttp.application.services.UuidGeneratorService;
import pe.puyu.pukahttp.application.services.printjob.deprecated.JTicketDesignPrintJob;
import pe.puyu.pukahttp.application.services.printjob.designer.SweetTicketDesigner;
import pe.puyu.pukahttp.application.services.printjob.output.MyPrinter;
import pe.puyu.pukahttp.domain.*;
import pe.puyu.pukahttp.domain.models.PrintDocument;
import pe.puyu.pukahttp.domain.models.PrintJob;

import java.util.LinkedList;
import java.util.List;

public class PrintJobService {
    private final FailedPrintJobsStorage failedPrintJobsStorage;
    private final AppNotifier notifier;
    private final SweetDefaultComponentsProvider defaultComponentsProvider;

    public PrintJobService(
        FailedPrintJobsStorage failedPrintJobsStorage,
        AppNotifier notifier,
        SweetDefaultComponentsProvider defaultComponentsProvider
    ) {
        this.failedPrintJobsStorage = failedPrintJobsStorage;
        this.notifier = notifier;
        this.defaultComponentsProvider = defaultComponentsProvider;
    }

    public void printTickets(String jsonArray) throws PrintJobException {
        JTicketDesignPrintJob.print(jsonArray);
    }

    public void printReport(String jsonObject) throws PrintJobException {
        JTicketDesignPrintJob.report(jsonObject);
    }

    public void openDrawer(String jsonObject) throws PrintJobException {
        JTicketDesignPrintJob.openDrawer(jsonObject);
    }

    public void print(PrintDocument document) throws DataValidationException, PrintJobException, PrintServiceNotFoundException {
        new PrintDocumentValidator(document).validate();
        try {
            MyPrinter printer = MyPrinter.from(document.printerInfo());
            SweetTicketDesigner designer = new SweetTicketDesigner(this.defaultComponentsProvider);
            printer.print(designer.design(document.jsonData(), document.times()));
        } catch (PrintJobException | PrintServiceNotFoundException e) {
            PrintJob printJob = new PrintJob(UuidGeneratorService.random(), document);
            failedPrintJobsStorage.save(printJob);
            notifier.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            notifier.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void reprint() {
        List<PrintJob> printJobs = failedPrintJobsStorage.getAllPrintJobs();
        List<PrintJob> willDeleteJobs = new LinkedList<>();
        for (PrintJob job : printJobs) {
            try {
                PrintDocumentValidator validator = new PrintDocumentValidator(job.document());
                validator.validate();
                MyPrinter printer = MyPrinter.from(job.document().printerInfo());
                SweetTicketDesigner designer = new SweetTicketDesigner(this.defaultComponentsProvider);
                printer.print(designer.design(job.document().jsonData(), job.document().times()));
                willDeleteJobs.add(job);
            } catch (Exception e) {
                notifier.error(e.getMessage());
            }
        }
        for (PrintJob job : willDeleteJobs) {
            failedPrintJobsStorage.delete(job);
        }
    }

    public void release() {
        failedPrintJobsStorage.deleteAll();
        notifier.info("The print queue has been released");
    }

}
