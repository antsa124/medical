package com.antsasdomain.medicalapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator {

    // Password regex: at least 8 characters, one uppercase, one number, one special char
    private static final String AT_LEAST_ONE_UPPERCASE = "(?=.*[A-Z])";
    private static final String AT_LEAST_ONE_DIGIT = "(?=.*\\d)";
    private static final String AT_LEAST_ONE_SPECIAL_CHAR = "(?=.*[@$!%*?&])";
    private static final String PASSWORD = "[A-Za-z\\d@$!%*?&]{8,}";

    private static final String PASS_PATTERN =
            "^"+AT_LEAST_ONE_UPPERCASE+AT_LEAST_ONE_DIGIT+AT_LEAST_ONE_SPECIAL_CHAR+PASSWORD+"$";

    private static final Pattern pattern = Pattern.compile(PASS_PATTERN);

    public static boolean isValid(String password) {
        return pattern.matcher(password).matches();
    }

    public static boolean isNotNull(String password) {
        return password != null && !password.isEmpty();
    }
}
