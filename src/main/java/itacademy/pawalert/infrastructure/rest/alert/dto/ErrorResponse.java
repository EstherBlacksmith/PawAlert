package itacademy.pawalert.infrastructure.rest.alert.dto;

public record ErrorResponse(int status, String error, String message) {
}