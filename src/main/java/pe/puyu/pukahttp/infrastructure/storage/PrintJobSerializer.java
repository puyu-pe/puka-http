package pe.puyu.pukahttp.infrastructure.storage;

import com.google.gson.*;
import pe.puyu.pukahttp.domain.models.PrintJob;

import java.lang.reflect.Type;

public class PrintJobSerializer implements JsonSerializer<PrintJob> {
    @Override
    public JsonElement serialize(PrintJob src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("id", src.id());

        JsonObject info = new JsonObject();
        info.addProperty("printerName", src.info().printerInfoOld().printerName());
        if (src.info().printerInfoOld().type() != null) {
            info.addProperty("type", src.info().printerInfoOld().type().toString());
        }
        if (src.info().printerInfoOld().port() != null) {
            info.addProperty("port", src.info().printerInfoOld().port());
        }
        if (src.info().times() != null) {
            info.addProperty("times", src.info().times());
        }

        JsonElement printDataJson = JsonParser.parseString(src.info().printData());
        info.add("printData", printDataJson);

        result.add("info", info);
        return result;
    }
}
