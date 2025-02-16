package com.antsasdomain.medicalapp.dto.prescriptionDTO;

import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationResponseDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientResponseDTO;
import com.antsasdomain.medicalapp.model.PrescriptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionForDoctorResponseDTO {

    private PatientResponseDTO patient;
    private LocalDate prescriptionDate;
    private PrescriptionType prescriptionType;
    private List<MedicationResponseDTO> medicine;
    private List<String> qrCodes;
    private String pharmacyCode;
}
