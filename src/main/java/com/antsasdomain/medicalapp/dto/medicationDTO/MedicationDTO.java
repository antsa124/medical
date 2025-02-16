package com.antsasdomain.medicalapp.dto.medicationDTO;

import com.antsasdomain.medicalapp.model.MedicationType;
import com.antsasdomain.medicalapp.validation.MedicationTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicationDTO {
    private String name;
    private String description;
    private String dosage;

    @JsonDeserialize(using = MedicationTypeDeserializer.class)
    private MedicationType medicationType;
}
