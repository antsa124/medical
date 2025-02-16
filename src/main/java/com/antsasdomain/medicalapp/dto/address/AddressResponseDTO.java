package com.antsasdomain.medicalapp.dto.address;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
