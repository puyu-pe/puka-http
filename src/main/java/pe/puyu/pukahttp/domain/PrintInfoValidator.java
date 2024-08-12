package pe.puyu.pukahttp.domain;

import pe.puyu.pukahttp.domain.models.PrintInfo;

public class PrintInfoValidator {
    private final PrintInfo data;

    public PrintInfoValidator(PrintInfo data) {
        this.data = data;
    }

    public void validate() throws DataValidationException {
        PrinterInfoValidator printerInfoValidator = new PrinterInfoValidator(data.printerInfo());
        printerInfoValidator.validate();
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
