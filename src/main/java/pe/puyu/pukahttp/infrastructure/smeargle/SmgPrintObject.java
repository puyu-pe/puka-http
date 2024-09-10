package pe.puyu.pukahttp.infrastructure.smeargle;

import com.google.gson.*;
import pe.puyu.pukahttp.infrastructure.smeargle.block.SmgBlock;
import pe.puyu.pukahttp.infrastructure.smeargle.drawer.SmgDrawer;
import pe.puyu.pukahttp.infrastructure.smeargle.properties.SmgProperties;
import pe.puyu.pukahttp.infrastructure.smeargle.styles.SmgMapStyles;

import java.util.Map;

public class SmgPrintObject {
    private final JsonObject object;
    private final JsonArray data;
    private final JsonObject metadata;

    private SmgPrintObject() {
        this.object = new JsonObject();
        this.data = new JsonArray();
        this.metadata = new JsonObject();
    }

    public SmgPrintObject setProperties(SmgProperties properties) {
        String jsonProperties = properties.toJson();
        if (jsonProperties != null) {
            JsonElement propertiesElement = JsonParser.parseString(jsonProperties);
            object.add("properties", propertiesElement);
        }
        return this;
    }

    public SmgPrintObject addInfo(String key, JsonElement value) {
        metadata.add(key, value);
        return this;
    }

    public static SmgPrintObject build() {
        return new SmgPrintObject();
    }

    public SmgPrintObject addBlock(SmgBlock block) {
        JsonObject blockElement = JsonParser.parseString(block.toJson()).getAsJsonObject();
        data.add(blockElement);
        return this;
    }

    public SmgPrintObject addText(String text) {
        data.add(text);
        return this;
    }

    public SmgPrintObject openDrawer(boolean value) {
        if (value) {
            object.add("openDrawer", new JsonObject());
        } else {
            object.remove("openDrawer");
        }
        return this;
    }

    public SmgPrintObject openDrawer(SmgDrawer drawer) {
        JsonElement drawerElement = JsonParser.parseString(drawer.toJson());
        object.add("openDrawer", drawerElement);
        return this;
    }

    public SmgPrintObject setStyles(SmgMapStyles styles) {
        if (!styles.isEmpty()) {
            String stylesJson = styles.toJson();
            if (!stylesJson.isEmpty()) {
                JsonElement stylesElement = JsonParser.parseString(stylesJson);
                object.add("styles", stylesElement);
            }
        }
        return this;
    }

    public String toJson() {
        JsonObject object = this.metadata.deepCopy();
        if (!data.isEmpty()) {
            object.add("data", data);
        }
        for (Map.Entry<String, JsonElement> entry : this.object.entrySet()) {
            object.add(entry.getKey(), entry.getValue());
        }

        return object.toString();
    }
}
