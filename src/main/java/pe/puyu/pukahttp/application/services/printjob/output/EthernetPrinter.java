package pe.puyu.pukahttp.application.services.printjob.output;

import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.PrintServiceNotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EthernetPrinter extends MyPrinter {
    private String ip;
    private int port;

    public EthernetPrinter(String service) {
        super(service);
        ip = service;
        port = 9100;
        if (service.contains(":")) {
            String[] split = service.split(":");
            ip = split[0];
            try {
                port = Integer.parseInt(split[1]);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void print(@NotNull ByteArrayOutputStream buffer) throws PrintServiceNotFoundException, PrintJobException {
        try (SocketOutputStream stream = new SocketOutputStream(ip, port)) {
            stream.write(buffer.toByteArray());
        } catch (IOException e) {
            throw new PrintServiceNotFoundException(e.getMessage(), e);
        }
    }
}
