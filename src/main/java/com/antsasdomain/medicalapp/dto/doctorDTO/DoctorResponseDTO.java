package com.antsasdomain.medicalapp.dto.doctorDTO;

import com.antsasdomain.medicalapp.dto.AbstractPersonResponseDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionForDoctorResponseDTO;
import com.antsasdomain.medicalapp.model.Doctor;
import com.antsasdomain.medicalapp.model.Prescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDTO extends AbstractPersonResponseDTO {

    private String officeName;
    private List<PrescriptionForDoctorResponseDTO> prescriptions;

    public DoctorResponseDTO(
            String username,
            String firstName,
            String lastName,
            String email,
            String phone,
            String officeName,
            List<PrescriptionForDoctorResponseDTO> prescriptions
    ) {
        super(username, firstName, lastName, email, phone);
        this.officeName = officeName;
        this.prescriptions = prescriptions;
    }
}
