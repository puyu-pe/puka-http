package pe.puyu.pukahttp.application.services.printjob;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import pe.puyu.SweetTicketDesign.application.builder.gson.GsonPrinterObjectBuilder;
import pe.puyu.SweetTicketDesign.application.components.DefaultComponentsProvider;
import pe.puyu.SweetTicketDesign.application.printer.escpos.EscPosPrinter;
import pe.puyu.SweetTicketDesign.domain.designer.SweetDesigner;
import pe.puyu.SweetTicketDesign.domain.printer.SweetPrinter;
import pe.puyu.pukahttp.application.notifier.AppNotifier;
import pe.puyu.pukahttp.application.services.UuidGeneratorService;
import pe.puyu.pukahttp.application.services.printjob.deprecated.JTicketDesignPrintJob;
import pe.puyu.pukahttp.application.services.printjob.output.EthernetOutputStream;
import pe.puyu.pukahttp.application.services.printjob.output.SambaOutputStream;
import pe.puyu.pukahttp.application.services.printjob.output.SerialOutputStream;
import pe.puyu.pukahttp.application.services.printjob.output.SystemPrinter;
import pe.puyu.pukahttp.domain.*;
import pe.puyu.pukahttp.domain.models.PrintInfo;
import pe.puyu.pukahttp.domain.models.PrintJob;
import pe.puyu.pukahttp.domain.models.PrinterType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PrintJobService {
    private final FailedPrintJobsStorage failedPrintJobsStorage;
    private final AppNotifier notifier;

    public PrintJobService(FailedPrintJobsStorage failedPrintJobsStorage, AppNotifier notifier) {
        this.failedPrintJobsStorage = failedPrintJobsStorage;
        this.notifier = notifier;
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

    public void print(PrintInfo info) throws DataValidationException, PrintJobException, PrintServiceNotFoundException {
        // validations
        PrintInfoValidator validator = new PrintInfoValidator(info);
        validator.validate();
        // initialization
        String target = info.printerInfoOld().printerName();
        PrinterType type = Optional.ofNullable(info.printerInfoOld().type()).orElse(PrinterType.SYSTEM);
        int port = Integer.parseInt(Optional.ofNullable(info.printerInfoOld().port()).orElse("9100"));
        int times = Integer.parseInt(Optional.ofNullable(info.times()).orElse("1"));
        ByteArrayOutputStream buffer = sweetDesign(info.printData(), times);
        try {
            printJob(target, port, type, buffer);
        } catch (PrintJobException | PrintServiceNotFoundException e) {
            PrintJob printJob = new PrintJob(UuidGeneratorService.random(), info);
            failedPrintJobsStorage.save(printJob);
            notifier.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            notifier.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void reprint() throws PrintJobException, PrintServiceNotFoundException {
        List<PrintJob> printJobs = failedPrintJobsStorage.getAllPrintJobs();
        PrintJobException jobException = null;
        PrintServiceNotFoundException printServiceNotFoundException = null;
        Exception genericException = null;
        List<PrintJob> willDeleteJobs = new LinkedList<>();
        for (PrintJob job : printJobs) {
            try {
                PrintInfoValidator validator = new PrintInfoValidator(job.info());
                validator.validate();
                String target = job.info().printerInfoOld().printerName();
                PrinterType type = Optional.ofNullable(job.info().printerInfoOld().type()).orElse(PrinterType.SYSTEM);
                int port = Integer.parseInt(Optional.ofNullable(job.info().printerInfoOld().port()).orElse("9100"));
                int times = Integer.parseInt(Optional.ofNullable(job.info().times()).orElse("1"));
                ByteArrayOutputStream buffer = sweetDesign(job.info().printData(), times);
                printJob(target, port, type, buffer);
                willDeleteJobs.add(job);
            } catch (PrintJobException e) {
                notifier.error(e.getMessage());
                jobException = e;
            } catch (PrintServiceNotFoundException e) {
                notifier.error(e.getMessage());
                printServiceNotFoundException = e;
            } catch (Exception e) {
                notifier.error(e.getMessage());
                genericException = e;
            }
        }
        for (PrintJob job : willDeleteJobs) {
            failedPrintJobsStorage.delete(job);
        }
        if (jobException != null) {
            throw jobException;
        }
        if (printServiceNotFoundException != null) {
            throw printServiceNotFoundException;
        }
        if (genericException != null) {
            throw new RuntimeException(genericException);
        }
    }

    public void release() {
        failedPrintJobsStorage.deleteAll();
        notifier.info("The print queue has been released");
    }

    private ByteArrayOutputStream sweetDesign(String jsonElement, int times) {
        JsonElement element = JsonParser.parseString(jsonElement);
        JsonArray arrayData = new JsonArray();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        SweetPrinter printer = new EscPosPrinter(buffer);
        if (element.isJsonObject()) {
            arrayData.add(element);
        } else if (element.isJsonArray()) {
            arrayData = element.getAsJsonArray();
        }
        for (JsonElement data : arrayData) {
            if (data.isJsonObject()) {
                GsonPrinterObjectBuilder builder = new GsonPrinterObjectBuilder(data.getAsJsonObject());
                SweetDesigner designer = new SweetDesigner(builder, printer, new DefaultComponentsProvider());
                for (int i = 0; i < times; i++) {
                    designer.paintDesign();
                }
            }
        }
        return buffer;
    }

    private void printJob(String target, int port, PrinterType type, ByteArrayOutputStream buffer) throws PrintServiceNotFoundException, PrintJobException {
        try {
            if (type.equals(PrinterType.ETHERNET)) {
                try (EthernetOutputStream stream = new EthernetOutputStream(target, port)) {
                    stream.write(buffer.toByteArray());
                }
            } else if (type.equals(PrinterType.SAMBA)) {
                try (SambaOutputStream stream = new SambaOutputStream(target)) {
                    stream.write(buffer.toByteArray());
                }
            } else if (type.equals(PrinterType.SERIAL)) {
                try (SerialOutputStream stream = new SerialOutputStream(target)) {
                    stream.write(buffer.toByteArray());
                }
            } else {
                new SystemPrinter(target).print(buffer.toByteArray());
            }
        } catch (IOException e) {
            throw new PrintJobException(String.format("Could not establish a connection to the printer %s, with message: %s", target, e.getMessage()), e);
        }
    }

}
