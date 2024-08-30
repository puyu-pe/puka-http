package pe.puyu.pukahttp.infrastructure.javalin.models;

public record DeprecateErrorResponse(
    String status,
    String message,
    String error
) {
}
