package pe.puyu.pukahttp.infrastructure.smeargle.block;

import org.jetbrains.annotations.NotNull;

public enum SmgQrType {
    IMG("IMG"),

    NATIVE("NATIVE");

    private final String value;

    SmgQrType(String value) {
        this.value = value;
    }

    public static @NotNull SmgQrType from(@NotNull String value) {
        for (SmgQrType align : SmgQrType.values()) {
            if (align.value.equalsIgnoreCase(value.trim())) {
                return align;
            }
        }
        return SmgQrType.IMG;
    }

    public String getValue() {
        return this.value;
    }

}
