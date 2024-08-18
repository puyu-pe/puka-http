package pe.puyu.pukahttp.infrastructure.loggin;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LogLevel {
    // Level error : TRACE DEBUG INFO WARN ERROR
    TRACE("TRACE"),
    DEBUG("DEBUG"),
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR");

    private final String value;

    LogLevel(String value) {
        this.value = value;
    }

    public static @NotNull LogLevel fromValue(@Nullable String value) {
        if (value != null) {
            for (LogLevel logLevel : LogLevel.values()) {
                if (logLevel.value.equalsIgnoreCase(value.trim())) {
                    return logLevel;
                }
            }
        }
        return LogLevel.TRACE;
    }

    public String getValue() {
        return this.value;
    }
}
