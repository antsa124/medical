package com.antsasdomain.medicalapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // allows to define a global exception handler that will be applied to all
// RestController components by capturing exceptions thrown by API
public class GlobalExceptionHandler {

    // Handle validation errors (e.g., @NotBlank, @Size, @NotNull)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handle generic exceptions (fallback for other errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalExceptions(Exception ex) {
        Map.of("Something went wrong", ex.getMessage());
        return new ResponseEntity<>(
                Map.of("something went wrong", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
