package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgImageBlock implements SmgBlock {
    private final JsonObject object;
    private final JsonObject imgObject;

    private SmgImageBlock() {
        object = new JsonObject();
        object.addProperty("type", "img");
        imgObject = new JsonObject();
    }

    public static SmgImageBlock builder() {
        return new SmgImageBlock();
    }

    public SmgImageBlock setPath(@NotNull String localPath) {
        imgObject.addProperty("path", localPath);
        return this;
    }

    public SmgImageBlock setClass(@NotNull String className) {
        imgObject.addProperty("class", className);
        return this;
    }

    public @NotNull JsonObject toJson() {
        if (imgObject.size() > 0) {
            object.add("img", imgObject);
        }
        return object;
    }
}
