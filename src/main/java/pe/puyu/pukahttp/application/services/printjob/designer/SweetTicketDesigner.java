package pe.puyu.pukahttp.application.services.printjob.designer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pe.puyu.SweetTicketDesign.application.builder.gson.GsonPrinterObjectBuilder;
import pe.puyu.SweetTicketDesign.application.components.DefaultComponentsProvider;
import pe.puyu.SweetTicketDesign.application.printer.escpos.EscPosPrinter;
import pe.puyu.SweetTicketDesign.domain.components.SweetDefaultComponentsProvider;
import pe.puyu.SweetTicketDesign.domain.designer.SweetDesigner;
import pe.puyu.SweetTicketDesign.domain.printer.SweetPrinter;

import java.io.ByteArrayOutputStream;

public class SweetTicketDesigner {
    private final SweetDefaultComponentsProvider componentsProvider;

    public SweetTicketDesigner(SweetDefaultComponentsProvider componentsProvider) {
        this.componentsProvider = componentsProvider;
    }

    public SweetTicketDesigner() {
        this.componentsProvider = new DefaultComponentsProvider();
    }

    public ByteArrayOutputStream design(String data, int times) {
        JsonObject printObject = JsonParser.parseString(data).getAsJsonObject();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        SweetPrinter printer = new EscPosPrinter(buffer);
        GsonPrinterObjectBuilder builder = new GsonPrinterObjectBuilder(printObject);
        SweetDesigner designer = new SweetDesigner(builder, printer, this.componentsProvider);
        for (int i = 0; i < times; i++) {
            designer.paintDesign();
        }
        return buffer;
    }
}
