package com.antsasdomain.medicalapp.dto.doctorDTO;

import com.antsasdomain.medicalapp.dto.AbstractUpdatePersonDTO;
import com.antsasdomain.medicalapp.model.Doctor;
import com.antsasdomain.medicalapp.model.Prescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateDTO extends AbstractUpdatePersonDTO {

    private String officeName;
    private List<Prescription> prescriptions;

    public DoctorUpdateDTO(
            String username,
            String password,
            String firstName,
            String lastName,
            String email,
            String phonee,
            String officeName,
            List<Prescription> prescriptions
    ) {
        super(username, password, firstName, lastName, email, phonee);
        this.officeName = officeName;
        this.prescriptions = prescriptions;
    }

    public DoctorUpdateDTO(
            String username,
            String password,
            String firstName,
            String lastName,
            String email,
            String phonee,
            String officeName
    ) {
        super(username, password, firstName, lastName, email, phonee);
        this.officeName = officeName;
    }
}
