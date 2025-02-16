package com.antsasdomain.medicalapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPersonDTO {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @Email(message = "Invalid e-mail format")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10-15 digits")
    @NotBlank(message = "Phone cannot be empty")
    private String phone;
}
