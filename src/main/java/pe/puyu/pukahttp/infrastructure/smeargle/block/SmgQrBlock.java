package pe.puyu.pukahttp.infrastructure.smeargle.block;


import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgQrBlock implements SmgBlock {
    private final JsonObject object;
    private final JsonObject qrObject;

    private SmgQrBlock() {
        object = new JsonObject();
        object.addProperty("type", "qr");
        qrObject = new JsonObject();
    }

    public static SmgQrBlock builder() {
        return new SmgQrBlock();
    }

    public SmgQrBlock setData(@NotNull String stringData) {
        qrObject.addProperty("data", stringData);
        return this;
    }

    public SmgQrBlock setClass(@NotNull String className) {
        qrObject.addProperty("class", className);
        return this;
    }

    public SmgQrBlock setCorrectionLevel(@NotNull SmgQrErrorLevel level) {
        qrObject.addProperty("correctionLevel", level.getValue());
        return this;
    }

    public SmgQrBlock setQrType(@NotNull SmgQrType type) {
        qrObject.addProperty("type", type.getValue());
        return this;
    }

    public @NotNull String toJson() {
        if (qrObject.size() > 0) {
            object.add("qr", qrObject);
        }
        return object.toString();
    }
}
