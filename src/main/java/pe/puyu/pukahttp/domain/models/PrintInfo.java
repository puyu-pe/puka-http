package pe.puyu.pukahttp.domain.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PrintInfo(
    @NotNull String target,
    @Nullable PrinterType type,
    @Nullable String port,
    @Nullable String times,
    @NotNull String printData
) {
}
