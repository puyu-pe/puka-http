package pe.puyu.pukahttp.infrastructure.storage;

import com.google.gson.*;
import pe.puyu.pukahttp.domain.models.*;

import java.lang.reflect.Type;

public class PrintJobDeserializer implements JsonDeserializer<PrintJob> {
    @Override
    public PrintJob deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String id = jsonObject.get("id").getAsString();
        JsonObject documentObject = jsonObject.getAsJsonObject("document");
        JsonObject printer = documentObject.getAsJsonObject("printer");
        String printerName = printer.get("name").getAsString();
        PrinterType printerType = PrinterType.from(printer.get("type").getAsString());
        int times = documentObject.get("times").getAsInt();
        JsonElement jsonData = documentObject.get("jsonData");
        String printData = jsonData.isJsonPrimitive()
            ? jsonData.getAsString()
            : new Gson().toJson(jsonData);
        PrinterInfo printerInfo = new PrinterInfo(printerName, printerType);
        PrintDocument document = new PrintDocument(printerInfo, printData, times);
        return new PrintJob(id, document);
    }
}
