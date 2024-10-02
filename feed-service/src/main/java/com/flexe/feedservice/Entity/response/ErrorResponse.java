package com.flexe.feedservice.Entity.response;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private final HttpStatus statusCode;
    private final String message;

    public ErrorResponse(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
    // Getter methods
    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}