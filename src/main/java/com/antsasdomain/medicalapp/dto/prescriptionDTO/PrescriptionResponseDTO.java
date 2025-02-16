package com.antsasdomain.medicalapp.dto.prescriptionDTO;

import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorResponseDTO;
import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationResponseDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientResponseDTO;
import com.antsasdomain.medicalapp.model.PrescriptionStatus;
import com.antsasdomain.medicalapp.model.PrescriptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponseDTO {

    private PatientResponseDTO patient;
    private DoctorResponseDTO doctor;
    private String patientInsuranceNumber; // Versichertern-Nr
    private LocalDate prescriptionDate;
    private PrescriptionType prescriptionType;
    private List<MedicationResponseDTO> medicine;
    private List<String> qrCodes;
    private String pharmacyCode;
    private PrescriptionStatus prescriptionStatus;
}
