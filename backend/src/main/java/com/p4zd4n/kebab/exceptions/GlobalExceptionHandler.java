package com.p4zd4n.kebab.exceptions;

import com.p4zd4n.kebab.responses.exceptions.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEmployeeNotFoundException(
            EmployeeNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Employee not found with email '{}'", exception.getEmail());

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

    @ExceptionHandler(EmployeeNotActiveException.class)
    public ResponseEntity<ExceptionResponse> handleEmployeeNotActiveException(
            EmployeeNotActiveException exception,
            HttpServletRequest request
    ) {
        log.error("Inactive employee attempted login with email '{}'", exception.getEmail());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("employee.notActive", null, locale);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.FORBIDDEN.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidCredentialsException(
            InvalidCredentialsException exception,
            HttpServletRequest request
    ) {
        log.error("Invalid credentials provided for email '{}'", exception.getEmail());

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

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ExceptionResponse> handleMissingRequestHeaderException(
            MissingRequestHeaderException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} without required header: {}", request.getRequestURI(), exception.getHeaderName());

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
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        log.error("Validation errors occurred at {}", request.getRequestURI());

        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                    error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "No message"
        ));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAcceptLanguageHeaderValue.class)
    public ResponseEntity<ExceptionResponse> handleInvalidAcceptLanguageHeaderValue(
            InvalidAcceptLanguageHeaderValue exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with invalid header: {}", request.getRequestURI(), exception.getInvalidValue());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .build());
    }
}
