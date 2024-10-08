package pe.puyu.pukahttp.infrastructure.smeargle.block;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgTextBlock implements SmgBlock {
    protected final JsonObject object;
    protected final JsonArray rows;

    private SmgTextBlock(@Nullable String separator) {
        this.object = new JsonObject();
        this.rows = new JsonArray();
        this.object.addProperty("type", "text");
        if (separator != null && !separator.trim().isEmpty()) {
            this.object.addProperty("separator", separator);
        }
    }

    public static SmgTextBlock builder(@Nullable String separator) {
        return new SmgTextBlock(separator);
    }

    public SmgTextBlock addText(@NotNull String text) {
        this.rows.add(text);
        return this;
    }

    public SmgTextBlock addRow(@NotNull SmgRow row) {
        this.rows.add(row.toJson());
        return this;
    }

    public SmgTextBlock addCell(@NotNull SmgCell cell) {
        this.rows.add(JsonParser.parseString(cell.toJson()).getAsJsonObject());
        return this;
    }

    public @NotNull String toJson() {
        if (!this.rows.isEmpty()) {
            this.object.add("rows", this.rows);
        }
        return this.object.toString();
    }
}
