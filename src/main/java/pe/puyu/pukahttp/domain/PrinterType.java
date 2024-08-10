package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PrinterType {
    SYSTEM("SYSTEM"),
    SAMBA("SAMBA"),
    SERIAL("SERIAL"),
    ETHERNET("ETHERNET");

    private final String value;

    PrinterType(String value) {
        this.value = value;
    }

    public static @NotNull PrinterType from(@Nullable String value) {
        if (value != null) {
            for (PrinterType type : PrinterType.values()) {
                if (type.value.equalsIgnoreCase(value.trim())) {
                    return type;
                }
            }
        }
        return PrinterType.SYSTEM;
    }

}
