package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SmgMapStyles {
    private final @NotNull JsonObject styles;

    public SmgMapStyles() {
        this.styles = new JsonObject();
    }

    public SmgMapStyles(@NotNull SmgMapStyles styles) {
        this.styles = Optional.ofNullable(styles.toJson()).orElse(new JsonObject()).deepCopy();
    }

    public void set(@NotNull String className, @NotNull SmgStyle style) {
        Optional.ofNullable(style.toJson()).ifPresent((json) -> this.styles.add(className, json));
    }

    public boolean has(@NotNull String className) {
        return this.styles.has(className);
    }

    public boolean isEmpty() {
        return this.styles.size() == 0;
    }

    public void remove(@NotNull String className) {
        this.styles.remove(className);
    }

    public @Nullable JsonObject toJson() {
        if (isEmpty()) {
            return null;
        }
        return this.styles;
    }
}
