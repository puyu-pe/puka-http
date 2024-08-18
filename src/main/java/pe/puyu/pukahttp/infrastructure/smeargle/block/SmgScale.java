package pe.puyu.pukahttp.infrastructure.smeargle.block;

import org.jetbrains.annotations.NotNull;

public enum SmgScale {

    SMOOTH("SMOOTH"),

    DEFAULT("DEFAULT"),

    FAST("FAST"),

    REPLICATE("REPLICATE"),

    AREA_AVERAGING("AREA_AVERAGING");

    private final String value;

    SmgScale(String value) {
        this.value = value;
    }

    public static @NotNull SmgScale from(@NotNull String value) {
        for (SmgScale scale : SmgScale.values()) {
            if (scale.value.equalsIgnoreCase(value.trim())) {
                return scale;
            }
        }
        return SmgScale.SMOOTH;
    }
    public String getValue() {
        return this.value;
    }

}
