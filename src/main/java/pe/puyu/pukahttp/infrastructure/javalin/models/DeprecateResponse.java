package pe.puyu.pukahttp.infrastructure.javalin.models;

public record DeprecateResponse (
    String status,
    String message,
    int data
){
}
