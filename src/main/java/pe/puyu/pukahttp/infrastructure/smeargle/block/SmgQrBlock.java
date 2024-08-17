package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgQrBlock implements SmgBlock {
    private final @NotNull JsonObject blockObject;
    private final @NotNull JsonObject qrObject;

    private SmgQrBlock() {
        this.blockObject = new JsonObject();
        this.qrObject = new JsonObject();
    }

    @Override
    public @Nullable JsonObject toJson() {
        if (qrObject.size() > 0) {
            blockObject.add("qr", qrObject);
        }
        if (blockObject.size() == 0) return null;
        return blockObject;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SmgQrBlock center(@NotNull String data) {
        SmgStyle style = SmgStyle.center();
        return builder().data(data).style(style).build();
    }

    public static SmgQrBlock right(@NotNull String data) {
        SmgStyle style = SmgStyle.right();
        return builder().data(data).style(style).build();
    }

    public static SmgQrBlock left(@NotNull String data) {
        SmgStyle style = SmgStyle.left();
        return builder().data(data).style(style).build();
    }

    public static SmgQrBlock build() {
        return builder().build();
    }

    public static class Builder {
        private final SmgQrBlock qrBlock;

        public Builder() {
            this.qrBlock = new SmgQrBlock();
        }

        public SmgQrBlock build() {
            return this.qrBlock;
        }

        public Builder data(@NotNull String data) {
            this.qrBlock.qrObject.addProperty("data", data);
            return this;
        }

        public Builder type(@NotNull SmgQrType type) {
            this.qrBlock.qrObject.addProperty("type", type.getValue());
            return this;
        }

        public Builder errorLevel(@NotNull SmgQrErrorLevel errorLevel) {
            this.qrBlock.qrObject.addProperty("correctionLevel", errorLevel.getValue());
            return this;
        }

        public Builder style(@NotNull SmgStyle style) {
            SmgMapStyles styles = new SmgMapStyles();
            styles.set("$qr", style);
            this.qrBlock.blockObject.add("styles", styles.toJson());
            return this;
        }

    }
}
