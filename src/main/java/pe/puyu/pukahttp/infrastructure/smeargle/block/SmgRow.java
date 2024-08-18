package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgRow {

    private final JsonArray row;

    public SmgRow() {
        this.row = new JsonArray();
    }

    public <T> SmgRow add(@NotNull T value) {
        add(new SmgCell<>(value));
        return this;
    }

    public <T> SmgRow add(@NotNull T value, @NotNull String className) {
        add(new SmgCell<>(value, className));
        return this;
    }

    public <T> SmgRow add(@NotNull SmgCell<T> cell) {
        this.row.add(cell.toJson());
        return this;
    }

    public @Nullable JsonArray toJson() {
        if (row.isEmpty()) {
            return null;
        }
        return this.row;
    }

}
