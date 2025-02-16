package com.antsasdomain.medicalapp.dto.pharmacistDTO;

import com.antsasdomain.medicalapp.dto.AbstractPersonResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacistResponseDTO extends AbstractPersonResponseDTO {
    private String pharmacyName;
    private String pharmacyCode;

    public PharmacistResponseDTO(
            String username,
            String firstName,
            String lastName,
            String email,
            String phone,
            String pharmacyName,
            String pharmacyCode) {
        super(username, firstName, lastName, email, phone);
        this.pharmacyName = pharmacyName;
        this.pharmacyCode = pharmacyCode;
    }
}
