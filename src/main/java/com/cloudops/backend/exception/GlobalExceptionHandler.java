
package com.cloudops.backend.exception;

import com.cloudops.backend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request) {

        int status = ex.getStatusCode().value();

        String error = HttpStatus.valueOf(status)
                .getReasonPhrase();

        ErrorResponse response = new ErrorResponse(
                status,
                error,
                ex.getReason(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(response);
    }
}