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
        info.addProperty("target", src.info().target());
        if (src.info().type() != null) {
            info.addProperty("type", src.info().type().toString());
        }
        if (src.info().port() != null) {
            info.addProperty("port", src.info().port());
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
