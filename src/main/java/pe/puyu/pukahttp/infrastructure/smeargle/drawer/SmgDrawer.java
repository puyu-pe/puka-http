package pe.puyu.pukahttp.infrastructure.smeargle.drawer;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class SmgDrawer {
    private final JsonObject object;

    private SmgDrawer() {
        this.object = new JsonObject();
    }

    public static SmgDrawer builder() {
        return new SmgDrawer();
    }

    public SmgDrawer pin(@NotNull SmgDrawerPin pin) {
        this.object.addProperty("pin", pin.getValue());
        return this;
    }

    public SmgDrawer t1(int t1) {
        int value = Math.min(Math.max(t1, 0), 255);
        this.object.addProperty("t1", value);
        return this;
    }

    public SmgDrawer t2(int t2) {
        int value = Math.min(Math.max(t2, 0), 255);
        this.object.addProperty("t2", value);
        return this;
    }

    public String toJson() {
        return this.object.toString();
    }
}
