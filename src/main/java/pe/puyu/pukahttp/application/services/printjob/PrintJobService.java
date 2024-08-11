package pe.puyu.pukahttp.application.services.printjob;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import pe.puyu.SweetTicketDesign.application.builder.gson.GsonPrinterObjectBuilder;
import pe.puyu.SweetTicketDesign.application.components.DefaultComponentsProvider;
import pe.puyu.SweetTicketDesign.application.printer.escpos.EscPosPrinter;
import pe.puyu.SweetTicketDesign.domain.designer.SweetDesigner;
import pe.puyu.SweetTicketDesign.domain.printer.SweetPrinter;
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
import java.util.List;
import java.util.Optional;

public class PrintJobService {
    private final FailedPrintJobsStorage failedPrintJobsStorage;

    public PrintJobService(FailedPrintJobsStorage failedPrintJobsStorage) {
        this.failedPrintJobsStorage = failedPrintJobsStorage;
    }

    public void printTickets(String jsonArray) throws PrintJobException {
        JTicketDesignPrintJob.print(jsonArray);
    }

    public void printReport(String jsonObject) throws PrintJobException {
        JTicketDesignPrintJob.report(jsonObject);
    }

    public void print(PrintInfo info) throws DataValidationException, PrintJobException, PrintServiceNotFoundException {
        // validations
        PrintDataValidator validator = new PrintDataValidator(info);
        validator.validate();
        // initialization
        String target = info.target();
        PrinterType type = Optional.ofNullable(info.type()).orElse(PrinterType.SYSTEM);
        int port = Integer.parseInt(Optional.ofNullable(info.port()).orElse("9100"));
        int times = Integer.parseInt(Optional.ofNullable(info.times()).orElse("1"));
        ByteArrayOutputStream buffer = sweetDesign(info.printData(), times);
        try {
            printJob(target, port, type, buffer);
        } catch (PrintJobException e) {
            PrintJob printJob = new PrintJob(UuidGeneratorService.random(), info);
            failedPrintJobsStorage.save(printJob);
            throw new PrintJobException(e.getMessage(), e);
        } catch (PrintServiceNotFoundException e) {
            PrintJob printJob = new PrintJob(UuidGeneratorService.random(), info);
            failedPrintJobsStorage.save(printJob);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void reprint() throws PrintJobException, PrintServiceNotFoundException {
        List<PrintJob> printJobs = failedPrintJobsStorage.getAllPrintJobs();
        PrintJobException jobException = null;
        PrintServiceNotFoundException printServiceNotFoundException = null;
        Exception genericException = null;
        for (PrintJob job : printJobs) {
            try {
                PrintDataValidator validator = new PrintDataValidator(job.info());
                validator.validate();
                String target = job.info().target();
                PrinterType type = Optional.ofNullable(job.info().type()).orElse(PrinterType.SYSTEM);
                int port = Integer.parseInt(Optional.ofNullable(job.info().port()).orElse("9100"));
                int times = Integer.parseInt(Optional.ofNullable(job.info().times()).orElse("1"));
                ByteArrayOutputStream buffer = sweetDesign(job.info().printData(), times);
                printJob(target, port, type, buffer);
                failedPrintJobsStorage.delete(job);
            } catch (PrintJobException e) {
                jobException = e;
            } catch (PrintServiceNotFoundException e) {
                printServiceNotFoundException = e;
            } catch (Exception e) {
                genericException = e;
            }
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
