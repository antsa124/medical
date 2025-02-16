package com.antsasdomain.medicalapp.dto;

import com.antsasdomain.medicalapp.model.MedicationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDTO {

    private String name;
    private String description;
    private String dosage;
    private MedicationType medicationType;
}
