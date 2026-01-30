package itacademy.pawalert.infrastructure.rest.dto;

public record ErrorResponse(int status, String error, String message) {}