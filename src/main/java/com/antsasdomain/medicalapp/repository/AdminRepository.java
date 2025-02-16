package com.antsasdomain.medicalapp.repository;

import com.antsasdomain.medicalapp.model.Admin;
import com.antsasdomain.medicalapp.model.AdminLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    // ✅ Checks if at least one SUPER_ADMIN exists
    boolean existsByAdminLevel(AdminLevel adminLevel);
}
