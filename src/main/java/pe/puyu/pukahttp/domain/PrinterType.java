package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public enum PrinterType {
    SYSTEM("SYSTEM"),
    SAMBA("SAMBA"),
    SERIAL("SERIAL"),
    ETHERNET("ETHERNET");

    private final String value;

    PrinterType(String value){
        this.value = value;
    }

    public static @NotNull PrinterType from(String value){
        for(PrinterType type : PrinterType.values()){
            if(type.value.equalsIgnoreCase(value.trim())){
                return type;
            }
        }
        return PrinterType.SYSTEM;
    }

}
