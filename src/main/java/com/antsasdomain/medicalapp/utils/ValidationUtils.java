package com.antsasdomain.medicalapp.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ValidationUtils {

    public static ResponseEntity<?> mapNotEmpty(String param) {
        return new ResponseEntity<>
                (Map.of("error", param + " cannot be blank"),
                        HttpStatus.BAD_REQUEST);
    }
}
