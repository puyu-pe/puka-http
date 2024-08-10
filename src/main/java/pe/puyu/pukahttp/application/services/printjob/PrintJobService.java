package pe.puyu.pukahttp.application.services.printjob;

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
import pe.puyu.pukahttp.domain.models.PrintJob;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class PrintJobService {
    private final FailedPrintJobsStorage failedPrintJobsStorage;

    public PrintJobService(FailedPrintJobsStorage failedPrintJobsStorage) {
        this.failedPrintJobsStorage = failedPrintJobsStorage;
    }

    public void printTickets(String jsonArray) throws PrintJobException {
        try {
            JTicketDesignPrintJob.print(jsonArray);
        } catch (PrintJobException e) {
            PrintJob printJob = new PrintJob(UuidGeneratorService.random(), jsonArray, LocalDateTime.now());
            failedPrintJobsStorage.save(printJob);
            throw e;
        }
    }

    public void printReport(String jsonObject) throws PrintJobException {
        try {
            JTicketDesignPrintJob.report(jsonObject);
        } catch (Exception e) {
            PrintJob printJob = new PrintJob(UuidGeneratorService.random(), jsonObject, LocalDateTime.now());
            failedPrintJobsStorage.save(printJob);
        }
    }

    public void print(PrintData data) throws DataValidationException, PrintJobException, PrintServiceNotFoundException {
        // validations
        PrintDataValidator validator = new PrintDataValidator(data);
        validator.validate();
        // initialization
        String target = data.target();
        PrinterType type = Optional.ofNullable(data.type()).orElse(PrinterType.SYSTEM);
        int port = Integer.parseInt(Optional.ofNullable(data.port()).orElse("9100"));
        int times = Integer.parseInt(Optional.ofNullable(data.times()).orElse("1"));
        String printObject = data.printObject();
        // design print object
        GsonPrinterObjectBuilder builder = new GsonPrinterObjectBuilder(printObject);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        SweetPrinter printer = new EscPosPrinter(buffer);
        SweetDesigner designer = new SweetDesigner(builder, printer, new DefaultComponentsProvider());
        for (int i = 0; i < times; i++) {
            designer.paintDesign();
        }
        try {
            printJob(target, port, type, buffer);
        } catch (PrintJobException e) {
            PrintJob printJob = new PrintJob(UuidGeneratorService.random(), printObject, LocalDateTime.now());
            failedPrintJobsStorage.save(printJob);
            throw new PrintJobException(e.getMessage(), e);
        } catch (PrintServiceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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
            throw new PrintJobException(String.format("Could not establish a connection to the printer with message: %s", e.getMessage()), e);
        }
    }

}
