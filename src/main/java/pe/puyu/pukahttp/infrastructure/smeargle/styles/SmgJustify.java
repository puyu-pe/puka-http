package pe.puyu.pukahttp.infrastructure.smeargle.styles;

import org.jetbrains.annotations.NotNull;

public enum SmgJustify {

    CENTER("center"),
    LEFT("left"),
    RIGHT("right");

    private final String value;

    SmgJustify(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static @NotNull SmgJustify from(@NotNull String value) {
        for (SmgJustify align : SmgJustify.values()) {
            if (align.value.equalsIgnoreCase(value.trim())) {
                return align;
            }
        }
        return SmgJustify.LEFT;
    }

}
