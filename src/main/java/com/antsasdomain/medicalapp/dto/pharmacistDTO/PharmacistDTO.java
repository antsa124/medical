package com.antsasdomain.medicalapp.dto.pharmacistDTO;

import com.antsasdomain.medicalapp.dto.AbstractPersonDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PharmacistDTO extends AbstractPersonDTO {
    private String pharmacyName;
    private String pharmacyCode; // Should match the code in Prescription

    public PharmacistDTO(
            String username,
            String password,
            String firstName,
            String lastName,
            String email,
            String phone,
            String pharmacyName,
            String pharmacyCode) {
        super(username, password, firstName, lastName, email, phone);
        this.pharmacyName = pharmacyName;
        this.pharmacyCode = pharmacyCode;
    }

}
