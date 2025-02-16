package com.antsasdomain.medicalapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends Person {

    @NotBlank(message = "Officename cannot be empty")
    private String officeName;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;

    public Doctor(
            String username,
            String password,
            String firstName,
            String lastName,
            String email,
            String phone,
            String officeName) {
        super(username, password,firstName, lastName, email, phone, PersonType.DOCTOR);
        this.officeName = officeName;
    }
}
