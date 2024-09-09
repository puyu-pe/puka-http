package pe.puyu.pukahttp.infrastructure.smeargle.styles;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SmgStyle {
    private JsonObject object;

    private SmgStyle() {
        this.object = new JsonObject();
    }

    public static SmgStyle builder() {
        return new SmgStyle();
    }

    public SmgStyle reset() {
        return this.reset(new JsonObject());
    }

    public SmgStyle reset(JsonObject object) {
        this.object = object;
        return this;
    }

    public String uniqueClassName() {
        JsonObject sortedObject = new JsonObject();
        this.object.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> sortedObject.add(entry.getKey(), entry.getValue()));

        StringBuilder className = new StringBuilder("_");
        sortedObject.entrySet().forEach(entry ->
            className.append(entry.getKey()).append("=").append(entry.getValue().getAsString()).append("_"));
        return className.toString();
    }

    public static SmgStyle copy(@NotNull SmgStyle style) {
        SmgStyle newStyle = new SmgStyle();
        newStyle.object = style.object.deepCopy();
        return newStyle;
    }

    public SmgStyle ifThen(boolean condition, @NotNull SmgStyle style) {
        if (condition) {
            this.merge(style);
        }
        return this;
    }

    public SmgStyle ifElse(boolean condition, @NotNull SmgStyle trueStyle, @NotNull SmgStyle falseStyle) {
        if (condition) {
            this.merge(trueStyle);
        } else {
            this.merge(falseStyle);
        }
        return this;
    }

    public SmgStyle merge(@NotNull SmgStyle parentStyle) {
        parentStyle.object.entrySet().forEach(entry -> this.object.add(entry.getKey(), entry.getValue()));
        return this;
    }

    public boolean isEmpty() {
        return this.object.size() == 0;
    }

    @NotNull
    public String toJson() {
        return this.object.toString();
    }

    public SmgStyle fontWidth(@Nullable Integer value) {
        if (value != null) {
            this.object.addProperty("fontWidth", Math.max(1, Math.min(value, 7)));
        }
        return this;
    }

    public SmgStyle fontHeight(@Nullable Integer value) {
        if (value != null) {
            this.object.addProperty("fontHeight", Math.max(1, Math.min(value, 7)));
        }
        return this;
    }

    public SmgStyle fontSize(@Nullable Integer value) {
        this.fontWidth(value).fontHeight(value);
        return this;
    }

    public SmgStyle bold() {
        return this.bold(true);
    }

    public SmgStyle bold(@Nullable Boolean value) {
        if (value != null) {
            this.object.addProperty("bold", value);
        }
        return this;
    }

    public SmgStyle normalize() {
        return this.normalize(true);
    }

    public SmgStyle normalize(@Nullable Boolean value) {
        if (value != null) {
            this.object.addProperty("normalize", value);
        }
        return this;
    }

    public SmgStyle bgInverted() {
        return this.bgInverted(true);
    }

    public SmgStyle bgInverted(@Nullable Boolean value) {
        if (value != null) {
            this.object.addProperty("bgInverted", value);
        }
        return this;
    }

    public SmgStyle pad(@Nullable String charValue) {
        if (charValue != null && !charValue.isEmpty()) {
            this.object.addProperty("pad", charValue.charAt(0));
        }
        return this;
    }

    public SmgStyle align(@Nullable SmgJustify justify) {
        if (justify != null) {
            this.object.addProperty("align", justify.getValue());
        }
        return this;
    }

    public SmgStyle center() {
        return this.align(SmgJustify.CENTER);
    }

    public SmgStyle left() {
        return this.align(SmgJustify.LEFT);
    }

    public SmgStyle right() {
        return this.align(SmgJustify.RIGHT);
    }

    public SmgStyle charxels(@Nullable Integer value) {
        if (value != null) {
            this.object.addProperty("charxels", Math.max(0, value));
        }
        return this;
    }

    public SmgStyle scale(@Nullable SmgScale value) {
        if (value != null) {
            this.object.addProperty("scale", value.getValue());
        }
        return this;
    }

    public SmgStyle width(@Nullable Integer value) {
        if (value != null) {
            this.object.addProperty("width", Math.max(0, value));
        }
        return this;
    }

    public SmgStyle height(@Nullable Integer value) {
        if (value != null) {
            this.object.addProperty("height", Math.max(0, value));
        }
        return this;
    }

    public SmgStyle size(@Nullable Integer value) {
        this.height(value).width(value);
        return this;
    }

    public SmgStyle charCode(@Nullable String value) {
        if (value != null) {
            this.object.addProperty("charCode", value);
        }
        return this;
    }
}
