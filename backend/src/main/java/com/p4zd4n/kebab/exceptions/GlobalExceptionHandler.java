package com.p4zd4n.kebab.exceptions;

import com.p4zd4n.kebab.responses.exceptions.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EmployeeNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleEmployeeNotFoundException(
            EmployeeNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler({EmployeeNotActiveException.class})
    public ResponseEntity<ExceptionResponse> handleEmployeeNotActiveException(
            EmployeeNotActiveException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.FORBIDDEN.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler({InvalidCredentialsException.class})
    public ResponseEntity<ExceptionResponse> handleInvalidCredentialsException(
            InvalidCredentialsException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .message(exception.getMessage())
                        .build());
    }
}
