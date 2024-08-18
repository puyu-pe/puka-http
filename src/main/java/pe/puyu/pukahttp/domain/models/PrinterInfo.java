package pe.puyu.pukahttp.domain.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PrinterInfo(
    @NotNull String printerName,
    @Nullable PrinterType type,
    @Nullable String port
) {
}
