package pe.puyu.pukahttp.domain;

import org.jetbrains.annotations.NotNull;

public record Serverconfig(
    @NotNull String ip,
    @NotNull String port
) {
}
