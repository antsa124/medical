package com.antsasdomain.medicalapp.dto.medicationDTO;

import com.antsasdomain.medicalapp.model.MedicationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationResponseDTO {

    private String name;
    private String description;
    private String dosage;
    private MedicationType medicationType;
}
