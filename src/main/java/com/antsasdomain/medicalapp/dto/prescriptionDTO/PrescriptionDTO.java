package com.antsasdomain.medicalapp.dto.prescriptionDTO;

import com.antsasdomain.medicalapp.dto.MedicationDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientDTO;
import com.antsasdomain.medicalapp.model.*;
import com.antsasdomain.medicalapp.validation.PrescriptionStatusDeserializer;
import com.antsasdomain.medicalapp.validation.PrescriptionTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {
    private PatientDTO patient;
    private DoctorDTO doctor;
    private String patientInsuranceNumber;
    private LocalDate prescriptionDate;
    @JsonDeserialize(using = PrescriptionTypeDeserializer.class)
    private PrescriptionType prescriptionType;
    private List<MedicationDTO> medicine;
    private List<String> qrCodes;
    private String pharmacyCode;
    @JsonDeserialize(using = PrescriptionStatusDeserializer.class)
    private PrescriptionStatus prescriptionStatus;
}
