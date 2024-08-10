package pe.puyu.pukahttp.domain;

public class PrintDataValidator {
    private final PrintData data;

    public PrintDataValidator(PrintData data) {
        this.data = data;
    }

    public void validate() throws DataValidationException {
        if (data.printData().isBlank()) {
            throw new DataValidationException("Print object cannot be empty");
        }
        if (data.target().isBlank()) {
            throw new DataValidationException(String.format("Printer: %s target can't be empty.", data.target()));
        }
        if (data.type() != null && data.type().equals(PrinterType.ETHERNET)) {
            ServerConfigDTO serverConfig = new ServerConfigDTO(data.target(), String.valueOf(data.port()));
            ServerConfigValidator serverConfigValidator = new ServerConfigValidator(serverConfig);
            serverConfigValidator.validateIp();
            if (data.port() != null) {
                serverConfigValidator.validatePort();
            }
        }
        if (data.times() != null) {
            int times;
            try {
                times = Integer.parseInt(data.times());
            } catch (NumberFormatException e) {
                throw new DataValidationException(String.format("times %s must be an integer.", data.times()));
            }
            if (times <= 0) {
                throw new DataValidationException("Times must be a positive integer greater than zero.");
            }
        }

    }
}
