package com.antsasdomain.medicalapp.repository;

import com.antsasdomain.medicalapp.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Integer> {
    List<Medication> findByNameIn(List<String> medicationNames);
    Optional<Medication> findByNameIgnoreCase(String name);
}
