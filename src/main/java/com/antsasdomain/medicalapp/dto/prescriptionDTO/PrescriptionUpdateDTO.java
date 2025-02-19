package com.antsasdomain.medicalapp.dto.prescriptionDTO;

import com.antsasdomain.medicalapp.dto.MedicationDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorUpdateDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientUpdateDTO;
import com.antsasdomain.medicalapp.model.PrescriptionStatus;
import com.antsasdomain.medicalapp.model.PrescriptionType;
import com.antsasdomain.medicalapp.validation.PrescriptionStatusDeserializer;
import com.antsasdomain.medicalapp.validation.PrescriptionTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionUpdateDTO {
    private PatientUpdateDTO patient;
    private DoctorUpdateDTO doctor;
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
