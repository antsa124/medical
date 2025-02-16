package com.antsasdomain.medicalapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pharmacist extends Person {

    @NotBlank(message = "Pharmacy name cannot be empty")
    private String pharmacyName; // The name of the pharmacy they work for

    @NotBlank(message = "Pharmacy code cannot be empty")
    private String pharmacyCode; // Should match the code in Prescription

    public Pharmacist(String username,
                      String password,
                      String firstName,
                      String lastName,
                      String email,
                      String phone,
                      String pharmacyName,
                      String pharmacyCode) {
        super(username, password, firstName, lastName, email, phone, PersonType.PHARMACIST);
        this.pharmacyName = pharmacyName;
        this.pharmacyCode = pharmacyCode;
    }
}

