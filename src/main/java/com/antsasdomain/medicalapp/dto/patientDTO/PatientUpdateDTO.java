package com.antsasdomain.medicalapp.dto.patientDTO;

import com.antsasdomain.medicalapp.dto.AbstractUpdatePersonDTO;
import com.antsasdomain.medicalapp.dto.address.AddressUpdateDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientUpdateDTO extends AbstractUpdatePersonDTO {

    private AddressUpdateDTO address;
    private LocalDate birthday;

    public PatientUpdateDTO(
            String username,
            String password,
            String firstName,
            String lastName,
            String email,
            String phone,
            AddressUpdateDTO address,
            LocalDate birthday
    ) {
        super(username, password, firstName, lastName, email, phone);
        this.address = address;
        this.birthday = birthday;
    }
}
