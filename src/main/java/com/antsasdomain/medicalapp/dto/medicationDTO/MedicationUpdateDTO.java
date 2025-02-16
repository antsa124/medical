package com.antsasdomain.medicalapp.dto.medicationDTO;

import com.antsasdomain.medicalapp.model.MedicationType;
import com.antsasdomain.medicalapp.validation.MedicationTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationUpdateDTO {

    private String name;
    private String description;
    private String dosage;

    @JsonDeserialize(using = MedicationTypeDeserializer.class)
    private MedicationType medicationType;
}
