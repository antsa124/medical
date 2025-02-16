package com.antsasdomain.medicalapp.validation;

public class AddressValidator {

    private static final String ZIP_CODE_REGEX = "[0-9]{5}";

    public static boolean isValidPatientInsuranceNumber(String zipcode) {
        return zipcode.matches(ZIP_CODE_REGEX);
    }

    public static boolean isNotNull(String param) {
        return param != null && !param.isEmpty();
    }
}
