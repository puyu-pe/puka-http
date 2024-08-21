package pe.puyu.pukahttp.application.services.printjob.output;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintServiceNotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SerialPrinter extends MyPrinter {

    public SerialPrinter(String portDescriptor) {
        super(portDescriptor);
    }

    @Override
    public void print(@NotNull ByteArrayOutputStream buffer) throws PrintServiceNotFoundException, PrintJobException {
        try (SerialOutputStream stream = new SerialOutputStream(service)) {
            stream.write(buffer.toByteArray());
        } catch (IOException e) {
            throw new PrintServiceNotFoundException(e.getMessage(), e);
        }
    }

}
