package com.antsasdomain.medicalapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbstractPersonResponseDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
