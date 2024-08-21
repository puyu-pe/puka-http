package pe.puyu.pukahttp.infrastructure.storage;

import com.google.gson.*;
import pe.puyu.pukahttp.domain.models.PrintInfo;
import pe.puyu.pukahttp.domain.models.PrintJob;
import pe.puyu.pukahttp.domain.models.PrinterInfoOld;
import pe.puyu.pukahttp.domain.models.PrinterType;

import java.lang.reflect.Type;

public class PrintJobDeserializer implements JsonDeserializer<PrintJob> {
    @Override
    public PrintJob deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String id = jsonObject.get("id").getAsString();

        JsonObject infoObject = jsonObject.getAsJsonObject("info");
        String printerName = infoObject.get("printerName").getAsString();
        PrinterType type = infoObject.has("type") ? PrinterType.valueOf(infoObject.get("type").getAsString()) : null;
        String port = infoObject.has("port") ? infoObject.get("port").getAsString() : null;
        String times = infoObject.has("times") ? infoObject.get("times").getAsString() : null;

        JsonElement printDataElement = infoObject.get("printData");
        String printData = printDataElement.isJsonPrimitive()
            ? printDataElement.getAsString()
            : new Gson().toJson(printDataElement);

        PrinterInfoOld printerInfoOld = new PrinterInfoOld(printerName, type, port);
        PrintInfo printInfo = new PrintInfo(printerInfoOld, times, printData);
        return new PrintJob(id, printInfo);
    }
}
