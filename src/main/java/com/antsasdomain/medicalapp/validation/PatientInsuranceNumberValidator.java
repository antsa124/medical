package com.antsasdomain.medicalapp.validation;

public class PatientInsuranceNumberValidator {

    private static final String REGEX_PATTERN_9 = "^[A-Z]\\d{9}$";
    private static final String REGEX_PATTERN_11 = "^[A-Z]\\d{11}";

    public static boolean isValidPatientInsuranceNumber(String patientInsuranceNumber) {
        return patientInsuranceNumber.matches(REGEX_PATTERN_9)
                || patientInsuranceNumber.matches(REGEX_PATTERN_11);
    }

    public static boolean isNotNull(String patientInsuranceNumber) {
        return patientInsuranceNumber != null;
    }

}
