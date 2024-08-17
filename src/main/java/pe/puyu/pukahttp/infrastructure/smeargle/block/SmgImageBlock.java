package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgImageBlock implements SmgBlock {
    private final @NotNull JsonObject blockObject;
    private @NotNull SmgMapStyles styles;

    private SmgImageBlock() {
        this.blockObject = new JsonObject();
        this.styles = new SmgMapStyles();
    }

    public static SmgImageBlock imgPath(@NotNull String path){
        return builder().imgPath(path).build();
    }

    public static Builder builder(){
        return new Builder();
    }

    public static SmgImageBlock build(){
        return builder().build();
    }

    @Override
    public @Nullable JsonObject toJson() {
        if(!styles.isEmpty()){
            this.blockObject.add("styles", styles.toJson());
        }
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
