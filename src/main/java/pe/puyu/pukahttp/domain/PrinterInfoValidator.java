package pe.puyu.pukahttp.domain;

import pe.puyu.pukahttp.domain.models.PrinterInfoOld;
import pe.puyu.pukahttp.domain.models.PrinterType;

public class PrinterInfoValidator {

    private final PrinterInfoOld printerInfoOld;

    public PrinterInfoValidator(PrinterInfoOld printerInfoOld) {
        this.printerInfoOld = printerInfoOld;
    }

    public void validate() throws DataValidationException{
        if (printerInfoOld.printerName().isBlank()) {
            throw new DataValidationException(String.format("Printer: %s printerName can't be empty.", printerInfoOld.printerName()));
        }
        if (printerInfoOld.type() != null && printerInfoOld.type().equals(PrinterType.ETHERNET)) {
            ServerConfigDTO serverConfig = new ServerConfigDTO(printerInfoOld.printerName(), String.valueOf(printerInfoOld.port()));
            ServerConfigValidator serverConfigValidator = new ServerConfigValidator(serverConfig);
            serverConfigValidator.validateIp();
            if (printerInfoOld.port() != null) {
                serverConfigValidator.validatePort();
            }
        }
    }

}
