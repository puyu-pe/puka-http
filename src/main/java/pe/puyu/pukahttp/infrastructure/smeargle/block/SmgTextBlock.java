package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SmgTextBlock implements SmgBlock {

    private final @NotNull JsonArray rows;
    private @NotNull SmgMapStyles styles;
    private final @NotNull JsonObject blockObject;

    private SmgTextBlock() {
        rows = new JsonArray();
        this.styles = new SmgMapStyles();
        this.blockObject = new JsonObject();
    }

    public SmgTextBlock line() {
        return line('-', SmgStyle.size(1));
    }

    public SmgTextBlock line(char symbol, @NotNull SmgStyle ownStyles) {
        String className = "$line_" + rows.size();
        styles.set(className, SmgStyle.builder(ownStyles).pad(symbol).span(1000).build());
        rows.add(new SmgCell<>("", className).toJson());
        return this;
    }

    public <T> SmgTextBlock text(T text) {
        rows.add(new SmgCell<>(text).toJson());
        return this;
    }

    public <T> SmgTextBlock text(T text, @NotNull SmgStyle ownStyles) {
        String className = "$text_" + rows.size();
        styles.set(className, ownStyles);
        rows.add(new SmgCell<>(text, className).toJson());
        return this;
    }

    public SmgTextBlock row(@NotNull SmgCell<?>... cells) {
        JsonArray row = new JsonArray();
        for (SmgCell<?> cell : cells) {
            row.add(cell.toJson());
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        return this;
    }

    public SmgTextBlock row(@NotNull List<Object> values, @NotNull SmgStyle ownStyles) {
        String className = "$row_" + rows.size();
        styles.set(className, ownStyles);
        return row(values, className);
    }

    public SmgTextBlock row(@NotNull Object[] values, @NotNull SmgStyle ownStyles) {
        return row(Arrays.stream(values).toList(), ownStyles);
    }

    public SmgTextBlock row(@NotNull List<Object> values, @NotNull String className) {
        JsonArray row = new JsonArray();
        for (Object value : values) {
            row.add(new SmgCell<>(value, className).toJson());
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        return this;
    }

    public SmgTextBlock row(Object[] values, @NotNull String className) {
        return row(Arrays.stream(values).toList(), className);
    }

    public SmgTextBlock row(Object ... values) {
        JsonArray row = new JsonArray();
        for (Object value : values) {
            row.add(value.toString());
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        return this;
    }

    public static SmgTextBlock builder(int nColumns, SmgMapStyles styles) {
        return builder().nColumns(nColumns).styles(styles).build();
    }

    public static SmgTextBlock build(){
        return builder().build();
    }

    @Override
    public @NotNull JsonObject toJson() {
        if (!rows.isEmpty() ) {
            blockObject.add("rows", rows);
        }
        if(!styles.isEmpty()){
            blockObject.add("styles", styles.toJson());
        }
        if(blockObject.size() == 0){
            return null;
        }
        return blockObject;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final SmgTextBlock textBlock;

        public Builder() {
            this.textBlock = new SmgTextBlock();
        }

        public SmgTextBlock build() {
            return textBlock;
        }

        public Builder gap(int gap) {
            this.textBlock.blockObject.addProperty("gap", Math.max(gap, 0));
            return this;
        }

        public Builder separator(char separator) {
            this.textBlock.blockObject.addProperty("separator", separator);
            return this;
        }

        public Builder nColumns(int nColumns) {
            this.textBlock.blockObject.addProperty("nColumns", Math.max(nColumns, 0));
            return this;
        }

        public Builder styles(@NotNull SmgMapStyles styles) {
            this.textBlock.styles = new SmgMapStyles(styles);
            return this;
        }

    }

}
