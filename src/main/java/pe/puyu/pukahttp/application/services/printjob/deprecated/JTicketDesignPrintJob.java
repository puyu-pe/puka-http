package pe.puyu.pukahttp.application.services.printjob.deprecated;

import com.github.anastaciocintra.escpos.EscPos;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pe.puyu.pukahttp.application.services.printjob.PrintJobException;
import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.SweetTablePrinter;
import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.SweetTicketPrinter;
import pe.puyu.pukahttp.application.services.printjob.deprecated.printer.Printer;

import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class JTicketDesignPrintJob {

    public static void print(String jsonArray) throws PrintJobException {
        JsonArray tickets = JsonParser.parseString(jsonArray).getAsJsonArray();
        for (JsonElement ticket : tickets) {
            try {
                var sweetTicketPrinter = new SweetTicketPrinter(ticket.getAsJsonObject());
                sweetTicketPrinter.print();
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException ignored) {

            } catch (Exception e) {
                throw new PrintJobException(e.getMessage(), e.getCause());
            }
        }
    }

    public static void report(String jsonObject) throws PrintJobException {
        JsonObject data = JsonParser.parseString(jsonObject).getAsJsonObject();
        try {
            SweetTablePrinter sweetTablePrinter = new SweetTablePrinter(data);
            sweetTablePrinter.print();
        } catch (Exception e) {
            throw new PrintJobException(e.getMessage(), e);
        }
    }

    public static void openDrawer(String jsonData) throws PrintJobException {
        try {
            JsonObject printer = JsonParser.parseString(jsonData).getAsJsonObject();
            var name_system = printer.get("name_system").getAsString();
            var port = printer.get("port").getAsInt();
            var type = printer.get("type").getAsString();
            OutputStream outputStream = Printer.getOutputStreamFor(name_system, port, type);
            try (var escpos = new EscPos(outputStream)) {
                escpos.pulsePin(EscPos.PinConnector.Pin_2, 120, 240);
                escpos.cut(EscPos.CutMode.FULL);
            }
        } catch (Exception e) {
            throw new PrintJobException(e.getMessage(), e);
        }
    }
}
