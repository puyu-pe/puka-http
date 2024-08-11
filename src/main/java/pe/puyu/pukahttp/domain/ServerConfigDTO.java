package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public record ServerConfigDTO(
    @NotNull String ip,
    @NotNull String port
) {
}
