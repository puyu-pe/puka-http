package pe.puyu.pukahttp.application.services.printjob.output;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

public class SambaOutputStream extends OutputStream {
    private final String resourcePath;
    private FileOutputStream outputStream = null;

    /**
     *resourcePath ejemplo: "\\\\192.168.1.53\\jpuka"
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
    public void write(byte @NotNull [] b) throws IOException {
        connect();
        outputStream.write(b);
    }

    private void connect() throws FileNotFoundException {
        if(outputStream == null){
            File smbFile = new File(resourcePath);
            this.outputStream = new FileOutputStream(smbFile);
        }
    }

    @Override
    public void close() throws IOException {
        if(outputStream != null){
            outputStream.close();
        }
    }

}
