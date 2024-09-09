package pe.puyu.pukahttp.infrastructure.smeargle.drawer;

public enum SmgDrawerPin {
    _2(2),
    _5(5);

    private final int value;

    SmgDrawerPin(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
