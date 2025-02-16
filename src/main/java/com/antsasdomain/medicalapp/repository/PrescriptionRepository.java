package com.antsasdomain.medicalapp.repository;

import com.antsasdomain.medicalapp.model.Doctor;
import com.antsasdomain.medicalapp.model.Medication;
import com.antsasdomain.medicalapp.model.Patient;
import com.antsasdomain.medicalapp.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    List<Prescription> findByPatient(Patient patient);
    List<Prescription> findByDoctor(Doctor doctor);
    Long countByMedicineContaining(Medication medicine);
    Optional<Prescription> findByIdAndDoctor(Integer id, Doctor doctor);
    List<Prescription> findByPharmacyCode(String pharmacyCode);
    Optional<Prescription> findByIdAndPharmacyCode(Integer id, String pharmacyCode);

}
