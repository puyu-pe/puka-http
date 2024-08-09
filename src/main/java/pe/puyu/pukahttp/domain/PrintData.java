package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public record PrintData(
    @NotNull String nameSystem,
    @NotNull PrinterType printerType,
    @NotNull Integer port,
    @NotNull String printObject
) {
}
