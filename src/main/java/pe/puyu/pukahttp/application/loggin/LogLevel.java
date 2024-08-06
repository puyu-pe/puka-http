package pe.puyu.pukahttp.application.loggin;

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

    public String getValue() {
        return this.value;
    }
}
