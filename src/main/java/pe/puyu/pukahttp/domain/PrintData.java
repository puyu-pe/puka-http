package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PrintData(
    @NotNull String target,
    @Nullable PrinterType type,
    @Nullable Integer port,
    @Nullable Integer times,
    @NotNull String printObject
) {
}
