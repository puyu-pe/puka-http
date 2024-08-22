package pe.puyu.pukahttp.domain;

import pe.puyu.pukahttp.domain.models.PrintDocument;

public class PrintDocumentValidator {
    private final PrintDocument document;

    public PrintDocumentValidator(PrintDocument document) {
        this.document = document;
    }

    public void validate() throws DataValidationException {
        new PrinterInfoValidator(document.printerInfo()).validate();
        if(document.times() <= 0){
            throw new DataValidationException("Times must be a positive integer greater than zero.");
        }
    }
}
