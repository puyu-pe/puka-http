package pe.puyu.pukahttp.infrastructure.properties;

public enum AppPropertyKey {

    TRAY_SUPPORT("tray.support"),
    LOG_LEVEL("log.level"),
    TRAY_NOTIFICATIONS("tray.notifications"),;

    private final String key;

    AppPropertyKey(String value) {
        this.key = value;
    }

    @Override
    public String toString() {
        return key;
    }

}
