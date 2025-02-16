package com.antsasdomain.medicalapp.dto.patientDTO;

import com.antsasdomain.medicalapp.dto.AbstractPersonResponseDTO;
import com.antsasdomain.medicalapp.dto.address.AddressResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO extends AbstractPersonResponseDTO {
    private AddressResponseDTO address;
    private LocalDate birthday;
    private String patientInsuranceNumber;

    public PatientResponseDTO(
            String username,
            String firstName,
            String lastName,
            String email,
            String phone,
            AddressResponseDTO address,
            LocalDate birthday,
            String patientInsuranceNumber) {
        super(username, firstName, lastName, email, phone);
        this.address = address;
        this.birthday = birthday;
        this.patientInsuranceNumber = patientInsuranceNumber;
    }
}
