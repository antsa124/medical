package com.antsasdomain.medicalapp.dto.patientDTO;

import com.antsasdomain.medicalapp.dto.AbstractPersonDTO;
import com.antsasdomain.medicalapp.dto.address.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO extends AbstractPersonDTO {
    private AddressDTO address;
    private LocalDate birthday;
    private String patientInsuranceNumber;

    public PatientDTO(
            String username,
            String password,
            String firstName,
            String lastName,
            String email,
            String phone,
            AddressDTO address,
            LocalDate birthday,
            String patientInsuranceNumber) {
        super(username, password, firstName, lastName, email, phone);
        this.address = address;
        this.birthday = birthday;
        this.patientInsuranceNumber = patientInsuranceNumber;
    }
}
