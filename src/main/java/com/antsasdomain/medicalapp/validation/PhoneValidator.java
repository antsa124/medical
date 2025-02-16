package com.antsasdomain.medicalapp.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidator {

    private static final String PHONE_REGEX = "^\\+?[0-9]{10,15}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    public static boolean isValid(String phoneNumber) {
        Matcher matcher = PHONE_PATTERN.matcher(phoneNumber);
        return matcher.matches(); // Checks if the input matches the regex
    }
}
