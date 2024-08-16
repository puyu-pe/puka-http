package pe.puyu.pukahttp.infrastructure.smeargle.properties;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgCutProperty {

    private final @NotNull JsonObject object;

    private SmgCutProperty() {
        this.object = new JsonObject();
    }

    public static Builder builder() {
        return new Builder();
    }

    public @Nullable JsonObject toJson() {
        if (object.size() == 0) return null;
        return object;
    }

    public static class Builder {

        private final SmgCutProperty cut;

        private Builder() {
            this.cut = new SmgCutProperty();
        }

        public SmgCutProperty build() {
            return this.cut;
        }

        public Builder feed(int feed) {
            cut.object.addProperty("feed", Math.min(Math.max(feed, 1), 255));
            return this;
        }

        public Builder mode(@NotNull SmgCutMode mode) {
            cut.object.addProperty("mode", mode.getValue());
            return this;
        }

    }

}
