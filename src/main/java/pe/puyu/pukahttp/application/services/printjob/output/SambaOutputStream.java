package pe.puyu.pukahttp.application.services.printjob.output;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

public class SambaOutputStream extends OutputStream {
    private final String resourcePath;
    private FileOutputStream outputStream = null;

    /**
     * resourcePath ejemplo: "\\\\192.168.1.53\\jpuka"
     */
    public SambaOutputStream(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public void write(int i) throws IOException {
        connect();
        outputStream.write(i);
    }

    @Override
    public void write(byte @NotNull [] b) throws PrintJobException {
        connect();
        try {
            outputStream.write(b);
        } catch (IOException e) {
            throw new PrintJobException(e.getMessage(), e);
        }
    }

    private void connect() throws PrintJobException {
        try {
            if (outputStream == null) {
                File smbFile = new File(resourcePath);
                this.outputStream = new FileOutputStream(smbFile);
            }
        } catch (FileNotFoundException e) {
            throw new PrintJobException(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
    }

}
