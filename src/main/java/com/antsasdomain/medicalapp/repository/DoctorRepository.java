package com.antsasdomain.medicalapp.repository;

import com.antsasdomain.medicalapp.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByUsername(String username);
    Optional<Doctor> findByEmail(String email);
}
