package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgCell {
    private final JsonObject object;

    public SmgCell(@NotNull String value, @Nullable String className) {
        object = new JsonObject();
        object.addProperty("text", value);
        if (className != null && !className.isEmpty()) {
            object.addProperty("class", className);
        }
    }

    public static SmgCell build(String value, @Nullable String className) {
        return new SmgCell(value, className);
    }

    public @Nullable JsonObject toJson() {
        if (this.object.size() == 0) {
            return null;
        }
        return this.object;
    }

}
