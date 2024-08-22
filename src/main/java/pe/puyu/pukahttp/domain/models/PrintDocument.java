package pe.puyu.pukahttp.domain.models;

import org.jetbrains.annotations.NotNull;

public record PrintDocument(
    @NotNull PrinterInfo printerInfo,
    @NotNull String jsonData,
    @NotNull Integer times
) {

}
