package com.antsasdomain.medicalapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends Person {

    @Enumerated(EnumType.STRING)
    private AdminLevel adminLevel; // 🔹 Differentiates types of admins

    public Admin(String username,
                 String password,
                 String firstName,
                 String lastName,
                 String email,
                 String phone,
                 AdminLevel adminLevel) {
        super(username, password, firstName, lastName, email, phone, PersonType.SUPER_ADMIN, true);
        this.adminLevel = adminLevel;
    }
}
