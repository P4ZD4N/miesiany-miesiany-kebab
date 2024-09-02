package com.p4zd4n.kebab.exceptions;

import com.p4zd4n.kebab.responses.exceptions.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({EmployeeNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleEmployeeNotFoundException(
            EmployeeNotFoundException exception,
            HttpServletRequest request
    ) {
        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("employee.notFound", null, locale);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(message)
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
            InvalidCredentialsException exception,
            HttpServletRequest request
    ) {
        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("employee.invalidCredentials", null, locale);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler({MissingRequestHeaderException.class})
    public ResponseEntity<ExceptionResponse> handleMissingRequestHeaderException(
            MissingRequestHeaderException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Missing required header")
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAcceptLanguageHeaderValue.class)
    public ResponseEntity<ExceptionResponse> handleInvalidAcceptLanguageHeaderValue(
            InvalidAcceptLanguageHeaderValue exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .build());
    }
}
