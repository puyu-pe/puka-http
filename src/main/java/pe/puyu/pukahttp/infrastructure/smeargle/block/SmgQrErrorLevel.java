package pe.puyu.pukahttp.infrastructure.smeargle.block;

public enum SmgQrErrorLevel {
    //7%
    L("L"),

    //15%
    M("M"),

    //25%
    Q("Q"),

    //30%
    H("H");

    private final String value;

    SmgQrErrorLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
