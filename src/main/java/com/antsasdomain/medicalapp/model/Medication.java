package com.antsasdomain.medicalapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;
    private String dosage;

    @Enumerated(EnumType.STRING)
    private MedicationType medicationType;

    public Medication(String name, String description, String dosage, MedicationType medicationType) {
        this.name = name;
        this.description = description;
        this.dosage = dosage;
        this.medicationType = medicationType;
    }
}