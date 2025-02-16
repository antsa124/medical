package com.antsasdomain.medicalapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractUpdatePersonDTO {

    private String username;

    private String password;
    private String firstName;
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10-15 digits")
    private String phone;
}
