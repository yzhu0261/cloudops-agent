
package com.cloudops.backend.dto;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path
) {
}