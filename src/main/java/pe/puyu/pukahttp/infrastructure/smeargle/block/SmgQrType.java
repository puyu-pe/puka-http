package pe.puyu.pukahttp.infrastructure.smeargle.block;

public enum SmgQrType {
    IMG("IMG"),

    NATIVE("NATIVE");

    private final String value;

    SmgQrType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
