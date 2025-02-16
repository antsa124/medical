package com.antsasdomain.medicalapp.dto.doctorDTO;

import com.antsasdomain.medicalapp.dto.AbstractPersonResponseDTO;
import com.antsasdomain.medicalapp.model.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorWithoutPrescriptionViewResponseDTO extends AbstractPersonResponseDTO {
    private String officeName;

    public DoctorWithoutPrescriptionViewResponseDTO(
            String username,
            String firstName,
            String lastName,
            String email,
            String phone,
            String officeName
    ) {
        super(username, firstName, lastName, email, phone);
        this.officeName = officeName;
    }
}
