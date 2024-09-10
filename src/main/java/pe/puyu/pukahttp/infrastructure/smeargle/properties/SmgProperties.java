package pe.puyu.pukahttp.infrastructure.smeargle.properties;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

public class SmgProperties {
    private final JsonObject object;

    private SmgProperties() {
        this.object = new JsonObject();
    }

    public static SmgProperties builder() {
        return new SmgProperties();
    }

    public SmgProperties setBlockWidth(int charxels) {
        this.object.addProperty("blockWidth", Math.max(0, charxels));
        return this;
    }

    public SmgProperties setCut(SmgCutProperty cutProperty) {
        String cutJson = cutProperty.toJson();
        if (cutJson != null) {
            JsonObject cutObject = JsonParser.parseString(cutJson).getAsJsonObject();
            this.object.add("cut", cutObject);
        }
        return this;
    }

    @Nullable
    public String toJson() {
        if (this.object.size() == 0) {
            return null;
        }
        return this.object.toString();
    }
}
