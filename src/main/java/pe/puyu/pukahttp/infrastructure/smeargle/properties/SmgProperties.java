package pe.puyu.pukahttp.infrastructure.smeargle.properties;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SmgProperties {

    private final @NotNull JsonObject object;

    private SmgProperties() {
        this.object = new JsonObject();
    }

    private SmgProperties(@NotNull SmgProperties properties) {
        this.object = properties.object.deepCopy();
    }

    public @Nullable JsonObject toJson() {
        if (object.size() == 0) return null;
        return object;
    }

    public static SmgProperties blockWidth(int blockWidth) {
        return builder().blockWidth(blockWidth).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@NotNull SmgProperties properties) {
        return new Builder(properties);
    }

    public static class Builder {

        private final SmgProperties properties;

        private Builder() {
            properties = new SmgProperties();
        }

        private Builder(SmgProperties properties) {
            this.properties = new SmgProperties(properties);
        }

        public SmgProperties build() {
            return properties;
        }

        public Builder blockWidth(int blockWidth) {
            blockWidth = Math.max(0, blockWidth);
            this.properties.object.addProperty("blockWidth", blockWidth);
            return this;
        }

        public Builder normalize(boolean normalize) {
            this.properties.object.addProperty("normalize", normalize);
            return this;
        }

        public Builder charCode(@NotNull String charCode) {
            this.properties.object.addProperty("charCode", charCode);
            return this;
        }

        public Builder cutProperty(@NotNull SmgCutProperty cutProperty) {
            Optional.ofNullable(cutProperty.toJson()).ifPresent(json -> this.properties.object.add("cut", json));
            return this;
        }

    }
}
