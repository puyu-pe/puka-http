package pe.puyu.pukahttp.domain;

import pe.puyu.pukahttp.domain.models.PrinterInfo;
import pe.puyu.pukahttp.domain.models.PrinterType;

public class PrinterInfoValidator {

    private final PrinterInfo printerInfo;

    public PrinterInfoValidator(PrinterInfo printerInfo) {
        this.printerInfo = printerInfo;
    }

    public void validate() throws DataValidationException{
        if (printerInfo.printerName().isBlank()) {
            throw new DataValidationException(String.format("Printer: %s printerName can't be empty.", printerInfo.printerName()));
        }
        if (printerInfo.type() != null && printerInfo.type().equals(PrinterType.ETHERNET)) {
            ServerConfigDTO serverConfig = new ServerConfigDTO(printerInfo.printerName(), String.valueOf(printerInfo.port()));
            ServerConfigValidator serverConfigValidator = new ServerConfigValidator(serverConfig);
            serverConfigValidator.validateIp();
            if (printerInfo.port() != null) {
                serverConfigValidator.validatePort();
            }
        }
    }

}
