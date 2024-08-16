package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgStyle {

    private final @NotNull JsonObject object;

    private SmgStyle() {
        this.object = new JsonObject();
    }

    private SmgStyle(@NotNull SmgStyle style) {
        this.object = style.object.deepCopy();
    }

    public @Nullable JsonObject toJson() {
        if (object.size() == 0) {
            return null;
        }
        return object;
    }

    public static SmgStyle fontWidth(int fontWidth) {
        return builder().fontWidth(fontWidth).build();
    }

    public static SmgStyle fontHeight(int fontHeight) {
        return builder().fontHeight(fontHeight).build();
    }

    public static SmgStyle size(int size) {
        return builder().size(size).build();
    }

    public static SmgStyle bold() {
        return builder().bold().build();
    }

    public static SmgStyle normalize() {
        return builder().normalize().build();
    }

    public static SmgStyle bgInverted(boolean bgInverted) {
        return builder().bgInverted(bgInverted).build();
    }

    public static SmgStyle pad(char pad) {
        return builder().pad(pad).build();
    }

    public static SmgStyle center() {
        return builder().center().build();
    }

    public static SmgStyle left() {
        return builder().left().build();
    }

    public static SmgStyle right() {
        return builder().right().build();
    }

    public static SmgStyle span(int span) {
        return builder().span(span).build();
    }

    public static SmgStyle scale(SmgScale scale) {
        return builder().scale(scale).build();
    }

    public static SmgStyle width(int width) {
        return builder().width(width).build();
    }

    public static SmgStyle height(int height) {
        return builder().height(height).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SmgStyle style) {
        return new Builder(style);
    }

    public static class Builder {

        private final SmgStyle style;

        public Builder() {
            style = new SmgStyle();
        }

        public Builder(@NotNull SmgStyle style) {
            this.style = new SmgStyle(style);
        }

        public SmgStyle build() {
            return style;
        }

        public Builder fontWidth(int fontWidth) {
            fontWidth = Math.min(Math.max(fontWidth, 0), 7);
            style.object.addProperty("fontWidth", fontWidth);
            return this;
        }

        public Builder fontHeight(int fontHeight) {
            fontHeight = Math.min(Math.max(fontHeight, 0), 7);
            style.object.addProperty("fontHeight", fontHeight);
            return this;
        }

        public Builder size(int size) {
            size = Math.min(Math.max(size, 0), 7);
            style.object.addProperty("fontWidth", size);
            style.object.addProperty("fontHeight", size);
            return this;
        }

        public Builder bold() {
            return bold(true);
        }

        public Builder bold(boolean bold) {
            style.object.addProperty("bold", bold);
            return this;
        }

        public Builder normalize() {
            return normalize(true);
        }

        public Builder normalize(boolean normalize) {
            style.object.addProperty("normalize", normalize);
            return this;
        }

        public Builder bgInverted(boolean bgInverted) {
            style.object.addProperty("bgInverted", bgInverted);
            return this;
        }

        public Builder pad(char pad) {
            style.object.addProperty("pad", pad);
            return this;
        }

        public Builder center() {
            style.object.addProperty("align", "center");
            return this;
        }

        public Builder left() {
            style.object.addProperty("align", "left");
            return this;
        }

        public Builder right() {
            style.object.addProperty("align", "right");
            return this;
        }

        public Builder span(int span) {
            style.object.addProperty("span", Math.max(span, 0));
            return this;
        }

        public Builder scale(SmgScale scale) {
            style.object.addProperty("scale", scale.getValue());
            return this;
        }

        public Builder width(int width) {
            style.object.addProperty("width", Math.max(width, 0));
            return this;
        }

        public Builder height(int height) {
            style.object.addProperty("height", Math.max(height, 0));
            return this;
        }

    }

}
