package pe.puyu.pukahttp.infrastructure.smeargle.properties;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public class SmgCutProperty {
    private final JsonObject object;

    private SmgCutProperty() {
        this.object = new JsonObject();
    }

    public static SmgCutProperty builder() {
        return new SmgCutProperty();
    }

    public SmgCutProperty feed(int feed) {
        int value = Math.min(Math.max(feed, 1), 255);
        this.object.addProperty("feed", value);
        return this;
    }

    public SmgCutProperty mode(SmgCutMode mode) {
        this.object.addProperty("mode", mode.getValue());
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

