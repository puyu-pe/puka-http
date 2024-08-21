package pe.puyu.pukahttp.domain.models;

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

    public static boolean isValid(@Nullable String value){
        if(value == null) return false;
        for (PrinterType type : PrinterType.values()) {
            if (type.value.equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
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
