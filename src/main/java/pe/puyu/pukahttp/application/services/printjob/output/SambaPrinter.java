package pe.puyu.pukahttp.application.services.printjob.output;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintServiceNotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SambaPrinter extends MyPrinter {

    public SambaPrinter(String resourcePath) {
        super(resourcePath);
    }

    @Override
    public void print(@NotNull ByteArrayOutputStream buffer) throws PrintServiceNotFoundException, PrintJobException {
        try (SambaOutputStream stream = new SambaOutputStream(this.service)) {
            stream.write(buffer.toByteArray());
        } catch (IOException e) {
            throw new PrintServiceNotFoundException(e.getMessage(), e);
        }
    }
}
