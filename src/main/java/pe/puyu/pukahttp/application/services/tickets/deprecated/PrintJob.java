package pe.puyu.pukahttp.application.services.tickets.deprecated;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import pe.puyu.pukahttp.application.services.tickets.PrintTicketException;
import pe.puyu.pukahttp.application.services.tickets.deprecated.printer.SweetTicketPrinter;

import java.util.concurrent.TimeUnit;

public class PrintJob {

    public static void print(String jsonArray) throws PrintTicketException{
        JsonArray tickets = JsonParser.parseString(jsonArray).getAsJsonArray();
        for (JsonElement ticket : tickets) {
            try {
                var sweetTicketPrinter = new SweetTicketPrinter(ticket.getAsJsonObject());
                sweetTicketPrinter.print();
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException ignored) {

            } catch (Exception e) {
                throw new PrintTicketException(e.getMessage(), e.getCause());
            }
        }
    }
}
