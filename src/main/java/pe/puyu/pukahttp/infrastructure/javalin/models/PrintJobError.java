package pe.puyu.pukahttp.infrastructure.javalin.models;

import org.jetbrains.annotations.NotNull;

public record PrintJobError(
    @NotNull String id,
    @NotNull String exception,
    @NotNull String message,
    @NotNull String observation
) {

    public static <T extends Exception> PrintJobError fromException(String id, T e, String observation) {
        return new PrintJobError(id, e.getClass().getSimpleName(), e.getMessage(), observation);
    }
}
