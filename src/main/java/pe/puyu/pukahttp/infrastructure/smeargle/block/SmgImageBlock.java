package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgImageBlock implements SmgBlock {
    private final @NotNull JsonObject blockObject;

    private SmgImageBlock() {
        this.blockObject = new JsonObject();
    }

    public static SmgImageBlock center(@NotNull String path){
        SmgStyle style = SmgStyle.center();
        return builder().imgPath(path).style(style).build();
    }

    public static SmgImageBlock right(@NotNull String path){
        SmgStyle style = SmgStyle.right();
        return builder().imgPath(path).style(style).build();
    }

    public static SmgImageBlock left(@NotNull String path){
        SmgStyle style = SmgStyle.left();
        return builder().imgPath(path).style(style).build();
    }

    public static Builder builder(){
        return new Builder();
    }

    public static SmgImageBlock build(){
        return builder().build();
    }

    @Override
    public @Nullable JsonObject toJson() {
        if(blockObject.size() == 0){
            return null;
        }
        return blockObject;
    }

    public static class Builder {
        private final SmgImageBlock imageBlock;

        public Builder() {
            this.imageBlock = new SmgImageBlock();
        }

        public SmgImageBlock build(){
            return imageBlock;
        }

        public Builder imgPath(@NotNull String path) {
            this.imageBlock.blockObject.addProperty("imgPath", path);
            return this;
        }

        public Builder style(@NotNull SmgStyle style) {
            SmgMapStyles styles = new SmgMapStyles();
            styles.set("$img", style);
            this.imageBlock.blockObject.add("styles", styles.toJson());
            return this;
        }
    }
}
