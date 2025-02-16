package com.antsasdomain.medicalapp.repository;

import com.antsasdomain.medicalapp.model.Pharmacist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PharmacistRepository extends JpaRepository<Pharmacist, Integer> {
    Optional<Pharmacist> findByUsername(String username);
    Optional<Pharmacist> findByEmail(String email);
    Optional<Pharmacist> findByPharmacyCode(String pharmacyCode);
}
