package pe.puyu.pukahttp.domain.models;

import org.jetbrains.annotations.NotNull;

public record PrinterInfo(
    @NotNull String name,
    @NotNull PrinterType type
) {
}
