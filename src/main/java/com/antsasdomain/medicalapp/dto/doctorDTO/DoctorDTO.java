package com.antsasdomain.medicalapp.dto.doctorDTO;

import com.antsasdomain.medicalapp.dto.AbstractPersonDTO;
import com.antsasdomain.medicalapp.model.Doctor;
import com.antsasdomain.medicalapp.model.Prescription;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO extends AbstractPersonDTO {
    private String officeName;

    private List<Prescription> prescriptions;

    public DoctorDTO(
            String username,
            String password,
            String firstName,
            String lastName,
            String email,
            String phone,
            String officeName,
            List<Prescription> prescriptions
    ) {
        super(username, password, firstName, lastName, email, phone);
        this.officeName = officeName;
        this.prescriptions = prescriptions;
    }
}
