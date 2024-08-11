package pe.puyu.pukahttp.domain.models;

import org.jetbrains.annotations.NotNull;


public record PrintJob(
    @NotNull String id,
    @NotNull PrintInfo info
) {
}
