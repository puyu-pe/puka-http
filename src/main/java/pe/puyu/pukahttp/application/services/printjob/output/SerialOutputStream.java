package pe.puyu.pukahttp.application.services.printjob.output;

import com.fazecast.jSerialComm.SerialPort;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class SerialOutputStream extends OutputStream {
    private final String portDescriptor;
    private SerialPort comPort;

    public SerialOutputStream(String portDescriptor) {
        this.portDescriptor = portDescriptor;
    }

    @Override
    public void write(int i) throws IOException {
        connect();
        comPort.getOutputStream().write(i);
    }

    @Override
    public void write(byte @NotNull [] b) throws IOException {
        connect();
        comPort.writeBytes(b, b.length);
    }

    @Override
    public void close() throws IOException {
        if (comPort != null) {
            comPort.closePort();
        }
    }

    private void connect() throws IOException {
        if (comPort == null) {
            comPort = SerialPort.getCommPort(portDescriptor);
            if (!comPort.openPort()) {
                throw new IOException(String.format("Com port: %s can't write.", portDescriptor));
            }
        }
    }
}
