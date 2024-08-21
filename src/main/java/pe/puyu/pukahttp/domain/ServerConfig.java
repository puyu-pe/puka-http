package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public record ServerConfig(
    @NotNull String ip,
    @NotNull String port
) {
}
