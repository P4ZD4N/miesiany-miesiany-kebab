package com.p4zd4n.kebab.exceptions;

import com.p4zd4n.kebab.exceptions.alreadyexists.*;
import com.p4zd4n.kebab.exceptions.expired.OtpExpiredException;
import com.p4zd4n.kebab.exceptions.failed.OtpRegenerationFailedException;
import com.p4zd4n.kebab.exceptions.invalid.*;
import com.p4zd4n.kebab.exceptions.notactive.EmployeeNotActiveException;
import com.p4zd4n.kebab.exceptions.notfound.*;
import com.p4zd4n.kebab.exceptions.notmatches.OtpNotMatchesException;
import com.p4zd4n.kebab.exceptions.others.ExcessBreadException;
import com.p4zd4n.kebab.exceptions.overlap.WorkScheduleTimeOverlapException;
import com.p4zd4n.kebab.responses.exceptions.ExceptionResponse;
import com.p4zd4n.kebab.responses.exceptions.ItemTypeExceptionResponse;
import jakarta.mail.MessagingException;
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

    @ExceptionHandler(InvalidClosingTimeException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidClosingTimeException(
            InvalidClosingTimeException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with closing hour earlier than opening hour", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidPhoneException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidPhoneException(
            InvalidPhoneException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with invalid phone", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("phone.invalidFormat", null, locale);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidEmailException(
            InvalidEmailException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with invalid email", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("email.invalidFormat", null, locale);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(OpeningHourNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleOpeningHourNotFoundException(
            OpeningHourNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with not existing closing hour on {}", request.getRequestURI(), exception.getDayOfWeek());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidCapacityException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidCapacityException(
            InvalidCapacityException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with invalid capacity", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidPriceException(
            InvalidPriceException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with invalid price", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(BeverageNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleBeverageNotFoundException(
            BeverageNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with not existing beverage: {}", request.getRequestURI(), exception.getBeverageName());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(AddonNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleAddonNotFoundException(
            AddonNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with not existing addon: {}", request.getRequestURI(), exception.getAddonName());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(IngredientNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleIngredientNotFoundException(
            IngredientNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with not existing ingredient: {}", request.getRequestURI(), exception.getIngredientName());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(MealNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleMealNotFoundException(
            MealNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with not existing meal: {}", request.getRequestURI(), exception.getMealName());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(ContactNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleContactNotFoundException(
            ContactNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with not existing contact: {}", request.getRequestURI(), exception.getContactType());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(JobOfferNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleJobOfferNotFoundException(
            JobOfferNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with not existing job offer: {}", request.getRequestURI(), exception.getPositionName());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(CvNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCvNotFoundException(
            CvNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with not existing cv", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(JobApplicationNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleJobApplicationNotFoundException(
            JobApplicationNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with not existing job application", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(BeverageAlreadyExistsException.class)
    public ResponseEntity<ItemTypeExceptionResponse> handleBeverageAlreadyExistsException(
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with existing beverage", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("beverage.alreadyExists", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ItemTypeExceptionResponse
                        .builder()
                        .itemType("beverage")
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(AddonAlreadyExistsException.class)
    public ResponseEntity<ItemTypeExceptionResponse> handleAddonAlreadyExistsException(
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with existing addon", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("addon.alreadyExists", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ItemTypeExceptionResponse
                        .builder()
                        .itemType("addon")
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(MealAlreadyExistsException.class)
    public ResponseEntity<ItemTypeExceptionResponse> handleMealAlreadyExistsException(
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with existing meal", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("meal.alreadyExists", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ItemTypeExceptionResponse
                        .builder()
                        .itemType("meal")
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(IngredientAlreadyExistsException.class)
    public ResponseEntity<ItemTypeExceptionResponse> handleIngredientAlreadyExistsException(
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with existing ingredient", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("ingredient.alreadyExists", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ItemTypeExceptionResponse
                        .builder()
                        .itemType("ingredient")
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(JobOfferAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleJobOfferAlreadyExistsException(
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with existing job offer", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("jobOffer.alreadyExists", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(ExcessBreadException.class)
    public ResponseEntity<ExceptionResponse> handleExcessBreadException(
            ExcessBreadException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with excess bread", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(MealPromotionNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleMealPromotionNotFoundException(
            MealPromotionNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with id of not existing meal promotion: {}", request.getRequestURI(), exception.getId());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(BeveragePromotionNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleBeveragePromotionNotFoundException(
            BeveragePromotionNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with id of not existing beverage promotion: {}", request.getRequestURI(), exception.getId());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(AddonPromotionNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleAddonPromotionNotFoundException(
            AddonPromotionNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with id of not existing addon promotion: {}", request.getRequestURI(), exception.getId());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(MealPromotionAlreadyExists.class)
    public ResponseEntity<ExceptionResponse> handleMealPromotionAlreadyExists(
            MealPromotionAlreadyExists exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with existing meal promotion for '{}'", request.getRequestURI(), exception.getMealName());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("mealPromotion.alreadyExists", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(SubscriberAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleSubscriberAlreadyExistsException(
            SubscriberAlreadyExistsException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with existing subscriber email '{}'", request.getRequestURI(), exception.getEmail());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("subscriber.alreadyExists", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<ExceptionResponse> handleMessagingException(HttpServletRequest request) {
        log.error("Attempted request to {} and unable to send email", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .message("Unable to send email!")
                        .build());
    }

    @ExceptionHandler(SubscriberNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleSubscriberNotFoundException(
            SubscriberNotFoundException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with email of not existing newsletter subscriber {}", request.getRequestURI(), exception.getEmail());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(OtpNotMatchesException.class)
    public ResponseEntity<ExceptionResponse> handleOtpNotMatchesException(HttpServletRequest request) {
        log.error("Attempted request to {} with not matching OTP", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("otp.notMatching", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleOtpExpiredException(HttpServletRequest request) {
        log.error("Attempted request to {} with expired OTP", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("otp.expired", null, locale);

        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.GONE.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(OtpRegenerationFailedException.class)
    public ResponseEntity<ExceptionResponse> handleOtpRegenerationFailedException(
            OtpRegenerationFailedException exception,
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} in less than {} seconds from last request", request.getRequestURI(), exception.getOtoRegenerationTimeSeconds());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("otp.regenerationFailed", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(WorkScheduleEntryAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleWorkScheduleEntryAlreadyExistsException(
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with existing work schedule entry", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("workScheduleEntry.alreadyExists", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(WorkScheduleTimeOverlapException.class)
    public ResponseEntity<ExceptionResponse> handleWorkScheduleTimeOverlapException(
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with work schedule entry, which time overlaps with an existing schedule entry", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("workScheduleEntry.timeOverlap", null, locale);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(InvalidTimeRangeException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTimeRangeException(
            HttpServletRequest request
    ) {
        log.error("Attempted request to {} with invalid time range", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("workSchedule.invalidTimeRange", null, locale);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(WorkScheduleEntryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleWorkScheduleEntryNotFoundException(HttpServletRequest request) {
        log.error("Attempted request to {} with id of not existing work schedule entry", request.getRequestURI());

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String message = messageSource.getMessage("workSchedule.notFound", null, locale);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse
                        .builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(message)
                        .build());
    }
}
