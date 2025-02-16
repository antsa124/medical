package com.antsasdomain.medicalapp.controller;

import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionResponseForUserDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientResponseDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientUpdateDTO;
import com.antsasdomain.medicalapp.model.Patient;
import com.antsasdomain.medicalapp.service.PatientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class PatientController {

    private PatientService patientService;

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllUsers() {
        logger.info("Fetch all users from database...");
        List<PatientResponseDTO> userDTOs = patientService.getAllUsers();
        logger.info("Fetched all users from database...");
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        logger.info("Fetch user by id from database...");
        ResponseEntity<?> user = patientService.getUserById(id);
        logger.info("Fetched user by id from database...");
        return user;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody PatientDTO userDTO) {
        logger.info("Create new user...");
        ResponseEntity<?> responseEntity = patientService.savePatient(userDTO);
        logger.info("User created");
        return responseEntity;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Integer id) {
        logger.info("Delete user by ID: {}", id);
        ResponseEntity<Map<String, String>> responseEntity = patientService.deleteUserById(id);
        logger.info("User deleted: {}", responseEntity.getBody());
        return responseEntity;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Valid @RequestBody PatientUpdateDTO patientUpdateDto, @PathVariable Integer id) {
        logger.info("Update user by ID: {}", id);
        ResponseEntity<?> response = patientService.updateUser(id, patientUpdateDto);
        logger.info("Updated User with ID {}", id);
        return response;
    }

    @GetMapping("/{id}/prescriptions")
    public ResponseEntity<?> getPrescriptionsByUserId(@PathVariable Integer id) {
        logger.info("Fetch user's prescriptions by user ID: {}", id);
        List<PrescriptionResponseForUserDTO> allPrescriptionsById =
                patientService.findAllPrescriptionsById(id);
        if (allPrescriptionsById != null && !allPrescriptionsById.isEmpty()) {
            logger.info("Prescriptions found for user with ID: {}", id);
            return new ResponseEntity<>(allPrescriptionsById, HttpStatus.OK);
        } else {
            logger.error("Prescriptions not found for user with ID: {}", id);
            return new ResponseEntity<>(Map.of("error",
                    "No prescription found for user with ID " + id),
                    HttpStatus.NOT_FOUND);
        }
    }

}
