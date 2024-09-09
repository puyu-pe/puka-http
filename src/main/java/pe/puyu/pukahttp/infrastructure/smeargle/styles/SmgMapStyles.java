package pe.puyu.pukahttp.infrastructure.smeargle.styles;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

public class SmgMapStyles {
    private final JsonObject object;
    private final SmgStyle globalStyle;

    public SmgMapStyles() {
        this.object = new JsonObject();
        this.globalStyle = SmgStyle.builder();
    }

    public SmgMapStyles addGlobalStyle(SmgStyle style) {
        this.globalStyle.merge(style);
        return this;
    }

    public SmgMapStyles set(String className, SmgStyle style) {
        if (!style.isEmpty()) {
            this.object.add(className, JsonParser.parseString(style.toJson()).getAsJsonObject());
        }
        return this;
    }

    public SmgStyle get(String className) {
        if (this.has(className)) {
            return SmgStyle.builder().reset(JsonParser.parseString(this.object.get(className).toString()).getAsJsonObject());
        }
        return null;
    }

    public SmgMapStyles remove(String className) {
        this.object.remove(className);
        return this;
    }

    public boolean has(String className) {
        return this.object.has(className);
    }

    public SmgMapStyles setIfNotExists(String className, SmgStyle style) {
        if (!this.has(className)) {
            this.set(className, style);
        }
        return this;
    }

    public boolean isEmpty() {
        return this.object.size() == 0 && this.globalStyle.isEmpty();
    }

    public @NotNull String toJson() {
        if (!this.globalStyle.isEmpty()) {
            this.set("*", this.globalStyle);
        }
        return this.object.toString();
    }
}
