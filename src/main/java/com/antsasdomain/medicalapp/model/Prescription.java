package com.antsasdomain.medicalapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "patient_id",
                nullable = false,
                foreignKey = @ForeignKey(
                        name = "fk_prescription_patient",
                        foreignKeyDefinition = "FOREIGN KEY (patient_id) REFERENCES patient (id) " +
                                "ON DELETE CASCADE"))
    private Patient patient;

    @ManyToOne
    @JoinColumn(name ="doctor_id", nullable=false)
    private Doctor doctor;

    private LocalDate prescriptionDate;

    @Enumerated(EnumType.STRING)
    private PrescriptionType prescriptionType;

    @ManyToMany
    @JoinTable(name = "prescription_medication", joinColumns = @JoinColumn(name =
            "prescription_id"), inverseJoinColumns=@JoinColumn(name = "medication_id"))
    private List<Medication> medicine;

    @ElementCollection
    private List<String> qrCodes;

    private String pharmacyCode; // the pharmacy where it should be redeemed

    private PrescriptionStatus prescriptionStatus;

    public Prescription(
            Patient patient,
            Doctor doctor,
            LocalDate prescriptionDate,
            PrescriptionType prescriptionType,
            List<Medication> medicine,
            List<String> qrCodes,
            String pharmacyCode,
            PrescriptionStatus prescriptionStatus
    ) {
        this.patient = patient;
        this.doctor = doctor;
        this.prescriptionDate = prescriptionDate;
        this.prescriptionType = prescriptionType;
        this.medicine = medicine;
        this.qrCodes = qrCodes;
        this.pharmacyCode = pharmacyCode;
        this.prescriptionStatus = prescriptionStatus;
    }

    public Prescription(
            Patient patient,
            Doctor doctor,
            LocalDate prescriptionDate,
            PrescriptionType prescriptionType,
            List<Medication> medicine,
            List<String> qrCodes,
            String pharmacyCode) {
        this.patient = patient;
        this.doctor = doctor;
        this.prescriptionDate = prescriptionDate;
        this.prescriptionType = prescriptionType;
        this.medicine = medicine;
        this.qrCodes = qrCodes;
        this.pharmacyCode = pharmacyCode;
        this.prescriptionStatus = PrescriptionStatus.ACTIVE;
    }
}
