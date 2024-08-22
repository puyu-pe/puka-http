package pe.puyu.pukahttp.infrastructure.storage;

import com.google.gson.*;
import pe.puyu.pukahttp.domain.models.PrintJob;

import java.lang.reflect.Type;

public class PrintJobSerializer implements JsonSerializer<PrintJob> {
    @Override
    public JsonElement serialize(PrintJob src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject printerInfo = new JsonObject();
        printerInfo.addProperty("name", src.document().printerInfo().name());
        printerInfo.addProperty("type", src.document().printerInfo().type().getValue());

        JsonElement printDataJson = JsonParser.parseString(src.document().jsonData());

        JsonObject document = new JsonObject();
        document.addProperty("times", src.document().times());
        document.add("printer", printerInfo);
        document.add("jsonData", printDataJson);

        JsonObject printJob = new JsonObject();
        printJob.addProperty("id", src.id());
        printJob.add("document", document);
        return printJob;
    }
}
