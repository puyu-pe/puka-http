package pe.puyu.pukahttp.application.services.tickets.deprecated;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pe.puyu.pukahttp.application.services.tickets.PrintJobException;
import pe.puyu.pukahttp.application.services.tickets.deprecated.printer.SweetTablePrinter;
import pe.puyu.pukahttp.application.services.tickets.deprecated.printer.SweetTicketPrinter;

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
}
