package pe.puyu.pukahttp.application.services.printjob.output;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintServiceNotFoundException;
import pe.puyu.pukahttp.domain.models.PrinterInfo;

import java.io.ByteArrayOutputStream;

public abstract class MyPrinter {

    protected final String service;

    public MyPrinter(String service) {
        this.service = service;
    }

    abstract public void print(@NotNull ByteArrayOutputStream buffer) throws PrintServiceNotFoundException, PrintJobException;


    public static MyPrinter from(PrinterInfo printerInfo){
        return switch (printerInfo.type()){
            case ETHERNET -> new EthernetPrinter(printerInfo.name());
            case SAMBA ->  new SambaPrinter(printerInfo.name());
            case SERIAL -> new SerialPrinter(printerInfo.name());
            default -> new SystemPrinter(printerInfo.name());
        };
    }

}
