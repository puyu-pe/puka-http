package pe.puyu.pukahttp.domain;

import pe.puyu.pukahttp.domain.models.PrinterInfo;
import pe.puyu.pukahttp.domain.models.PrinterType;

public class PrinterInfoValidator {

    private final PrinterInfo printerInfo;

    public PrinterInfoValidator(PrinterInfo printerInfo) {
        this.printerInfo = printerInfo;
    }

    public void validate() throws DataValidationException {
        if (printerInfo.name().isBlank()) {
            throw new DataValidationException(String.format("Printer: %s printerName can't be empty.", printerInfo.name()));
        }
        if (printerInfo.type().equals(PrinterType.ETHERNET)) {
            ServerConfigValidator serverConfigValidator = new ServerConfigValidator(printerInfo.name());
            serverConfigValidator.validateIp();
            serverConfigValidator.validatePort();
        }
    }

}
