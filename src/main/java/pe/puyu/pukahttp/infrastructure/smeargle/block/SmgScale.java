package pe.puyu.pukahttp.infrastructure.smeargle.block;

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

    public String getValue() {
        return this.value;
    }

}
