package pe.puyu.pukahttp.infrastructure.smeargle.block;

import org.jetbrains.annotations.NotNull;

public enum SmgQrErrorLevel {

    //25%
    Q("Q"),

    //7%
    L("L"),

    //15%
    M("M"),

    //30%
    H("H");

    private final String value;

    SmgQrErrorLevel(String value) {
        this.value = value;
    }

    public static @NotNull SmgQrErrorLevel from(@NotNull String value) {
        for (SmgQrErrorLevel level : SmgQrErrorLevel.values()) {
            if (level.value.equalsIgnoreCase(value.trim())) {
                return level;
            }
        }
        return SmgQrErrorLevel.Q;
    }

    public String getValue() {
        return this.value;
    }

}
