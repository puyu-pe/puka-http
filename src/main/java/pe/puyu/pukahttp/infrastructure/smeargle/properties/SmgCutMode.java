package pe.puyu.pukahttp.infrastructure.smeargle.properties;

public enum SmgCutMode {
    FULL("FULL"),
    PART("PART");

    private final String value;

    SmgCutMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
