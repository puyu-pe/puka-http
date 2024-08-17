package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmgCell<T> {
    private final @NotNull T value;
    private final @Nullable String className;

    public SmgCell(@NotNull T value, @NotNull String className) {
        this.value = value;
        this.className = className;
    }

    public SmgCell(@NotNull T value) {
        this.value = value;
        this.className = null;
    }

    public @NotNull JsonObject toJson() {
        JsonObject cell = new JsonObject();
        cell.addProperty("text", value.toString());
        if (this.className != null) {
            cell.addProperty("class", className);
        }
        return cell;
    }

}
