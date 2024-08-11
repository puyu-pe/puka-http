package pe.puyu.pukahttp.application.services.printjob;

import java.io.IOException;

public class PrintJobException extends IOException {
    public PrintJobException(String message) {
        super(message);
    }

    public PrintJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
