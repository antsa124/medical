package com.antsasdomain.medicalapp.repository;

import com.antsasdomain.medicalapp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByUsername(String username);
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByPatientInsuranceNumber(String patientInsuranceNumber);
}
