package pe.puyu.pukahttp.application.services.printjob.output;

import com.fazecast.jSerialComm.SerialPort;
import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;

import java.io.IOException;
import java.io.OutputStream;

public class SerialOutputStream extends OutputStream {
    private final String portDescriptor;
    private SerialPort comPort;

    public SerialOutputStream(String portDescriptor) {
        this.portDescriptor = portDescriptor;
    }

    @Override
    public void write(int i) throws PrintJobException {
        connect();
        try {
            comPort.getOutputStream().write(i);
        } catch (IOException e) {
            throw new PrintJobException(e.getMessage(), e);
        }
    }

    @Override
    public void write(byte @NotNull [] b) throws PrintJobException {
        connect();
        comPort.writeBytes(b, b.length);
    }

    @Override
    public void close() throws IOException {
        if (comPort != null) {
            comPort.closePort();
        }
    }

    private void connect() throws PrintJobException {
        if (comPort == null) {
            comPort = SerialPort.getCommPort(portDescriptor);
            if (!comPort.openPort()) {
                throw new PrintJobException(String.format("Com port: %s can't write.", portDescriptor));
            }
        }
    }
}
