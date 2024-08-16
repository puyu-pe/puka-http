package pe.puyu.pukahttp.infrastructure.smeargle.block;

public enum SmgJustify {

    CENTER("center"),
    LEFT("left"),
    RIGHT("right");

    private final String value;

    SmgJustify(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
