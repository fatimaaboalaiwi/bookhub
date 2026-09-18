package com.company.bookhub.common.handler;

import com.company.bookhub.common.dto.ErrorResponse;
import com.company.bookhub.common.exception.DuplicateResourceException;
import com.company.bookhub.common.exception.ResourceNotFoundException;
import com.company.bookhub.common.i18n.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.company.bookhub.common.exception.NumverifyException;

import java.time.LocalDateTime;
import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;


    // =========================================
    // RESOURCE NOT FOUND
    // =========================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            Locale locale) {

        String message = messageService.getMessage(
                ex.getMessage(),
                locale
        );

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                message,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.NOT_FOUND
        );
    }


    // =========================================
    // DUPLICATE RESOURCE
    // =========================================

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            Locale locale) {

        String message = messageService.getMessage(
                ex.getMessage(),
                locale
        );

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                message,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.CONFLICT
        );
    }


    // =========================================
    // VALIDATION
    // =========================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            Locale locale) {

        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        String message = messageService.getMessage(
                "validation.failed",
                locale
        );

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message + ": " + errorMessage,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.BAD_REQUEST
        );
    }


    // =========================================
    // ACCESS DENIED
    // =========================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            Locale locale) {

        String message = messageService.getMessage(
                "access.denied",
                locale
        );

        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                message,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.FORBIDDEN
        );
    }


    // =========================================
    // INVALID LOGIN CREDENTIALS
    // =========================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            Locale locale) {

        String message = messageService.getMessage(
                "auth.invalid_credentials",
                locale
        );

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                message,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.UNAUTHORIZED
        );
    }


    // =========================================
    // GENERIC EXCEPTION
    // =========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            Locale locale) {

        String message = messageService.getMessage(
                "unexpected.error",
                locale
        );

        log.error("---- error: ", ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    @ExceptionHandler(NumverifyException.class)
    public ResponseEntity<ErrorResponse> handleNumverifyException(
            NumverifyException ex,
            Locale locale) {

        String message = messageService.getMessage(
                ex.getMessage(),
                locale
        );

        ErrorResponse error = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                message,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }
}