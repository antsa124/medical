package com.antsasdomain.medicalapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Address Street cannot be empty")
    private String street;

    @NotBlank(message = "Address City cannot be empty")
    private String city;

    @NotBlank(message = "Address State cannot be empty")
    private String state;

    @Pattern(regexp = "^[0-9]{5}$", message="Zipcode must have exactly 5 digits")
    @NotBlank(message = "Zipcode cannot be empty")
    private String zipCode;

    @NotBlank(message = "Country cannot be empty")
    private String country;


    public Address(String street, String city, String state, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }
}
