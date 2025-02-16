package com.antsasdomain.medicalapp.controller;

import com.antsasdomain.medicalapp.dto.auth.RegistrationDTO;
import com.antsasdomain.medicalapp.model.Admin;
import com.antsasdomain.medicalapp.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDTO registrationDTO) {
        return adminService.registerAdmin(registrationDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        logger.info("Deleting admin with id {}", id);
        ResponseEntity<?> response = adminService.deleteAdmin(id);
        logger.info("Deleted admin with id {}", id);
        return response;
    }

    @PutMapping("/approve/{username}")
    public ResponseEntity<?> approveNewUser(@PathVariable String username) {
        logger.info("Approving new user {}", username);
        ResponseEntity<?> responseEntity = adminService.approveUser(username);
        logger.info("Approved new user {}", username);
        return responseEntity;
    }


}
