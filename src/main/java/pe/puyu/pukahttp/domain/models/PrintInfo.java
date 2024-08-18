package pe.puyu.pukahttp.domain.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PrintInfo(
    @NotNull PrinterInfo printerInfo,
    @Nullable String times,
    @NotNull String printData
) {
}
