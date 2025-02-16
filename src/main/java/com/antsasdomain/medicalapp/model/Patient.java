package com.antsasdomain.medicalapp.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends Person {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    @Valid // ensures the object Address is validated too
    private Address address;

    @Past(message = "Birthday must be a past date")
    @NotNull(message = "Birthday cannot be empty")
    private LocalDate birthday;

    @NotBlank(message = "Patient Insurance Number cannot be empty")
    private String patientInsuranceNumber;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions;
    public Patient(String username,
                String password,
                String firstName,
                String lastName,
                String email,
                String phone,
                Address address,
                LocalDate birthday,
                String patientInsuranceNumber) {
        super(username, password,firstName, lastName, email, phone, PersonType.PATIENT, true);
        this.patientInsuranceNumber = patientInsuranceNumber;
        this.address = address;
        this.birthday = birthday;
    }
}
