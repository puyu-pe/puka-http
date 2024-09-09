package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgRow {
    private final JsonArray row;

    public SmgRow() {
        this.row = new JsonArray();
    }

    public SmgRow add(@NotNull String text, @Nullable String className) {
        SmgCell cell = SmgCell.build(text, className);
        this.row.add(JsonParser.parseString(cell.toJson()).getAsJsonObject());
        return this;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public int size() {
        return this.row.size();
    }

    public @NotNull JsonArray toJson() {
        return this.row;
    }

}
