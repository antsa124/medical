package com.antsasdomain.medicalapp.dto.auth;


import com.antsasdomain.medicalapp.dto.address.AddressDTO;
import com.antsasdomain.medicalapp.model.AdminLevel;
import com.antsasdomain.medicalapp.model.PersonType;
import com.antsasdomain.medicalapp.validation.AdminLevelDeserializer;
import com.antsasdomain.medicalapp.validation.PersonTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    private String email;
    private String phone;

    @JsonDeserialize(using = PersonTypeDeserializer.class)
    private PersonType personType; // "DOCTOR", "PHARMACIST", "PATIENT", "ADMIN"

    private String officeName; // case DOCTOR

    private String pharmacyName; // case PHARMACIST
    private String pharmacyCode; // case PHARMACIST

    private AddressDTO address; // case PATIENT
    private LocalDate birthday; // case PATIENT
    private String patientInsuranceNumber; // case PATIENT

    @JsonDeserialize(using = AdminLevelDeserializer.class)
    private AdminLevel adminLevel; // case ADMIN
}

