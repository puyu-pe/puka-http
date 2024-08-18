package pe.puyu.pukahttp.application.services.printjob;

import java.io.IOException;

public class PrintServiceNotFoundException extends IOException {
    public PrintServiceNotFoundException(String message) {
        super(message);
    }

    public PrintServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
