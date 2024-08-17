package pe.puyu.pukahttp.infrastructure.smeargle.drawer;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgDrawer {
    private final @NotNull JsonObject drawerObject;

    private SmgDrawer() {
        this.drawerObject = new JsonObject();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SmgDrawer pin(@NotNull Pin pin) {
        return builder().pin(pin).build();
    }

    public @Nullable JsonObject toJson() {
        if (drawerObject.size() == 0) {
            return null;
        }
        return drawerObject;
    }

    public static class Builder {

        private final SmgDrawer drawer;

        private Builder() {
            drawer = new SmgDrawer();
        }

        public Builder pin(@NotNull Pin pin) {
            drawer.drawerObject.addProperty("pin", pin.getValue());
            return this;
        }

        public Builder t1(int t1) {
            drawer.drawerObject.addProperty("t1", Math.min(Math.max(t1, 0), 255));
            return this;
        }

        public Builder t2(int t2) {
            drawer.drawerObject.addProperty("t2", Math.min(Math.max(t2, 0), 255));
            return this;
        }

        public SmgDrawer build() {
            return drawer;
        }

    }

    public enum Pin {
        _2(2),
        _5(5);

        private final int value;

        Pin(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
