package pe.puyu.pukahttp.domain.models;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public record PrintJob(
    @NotNull Long id,
    @NotNull String data,
    @NotNull LocalDateTime createdAt
) {
}
