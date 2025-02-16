package com.antsasdomain.medicalapp.controller;

import com.antsasdomain.medicalapp.dto.pharmacistDTO.PharmacistDTO;
import com.antsasdomain.medicalapp.dto.pharmacistDTO.PharmacistResponseDTO;
import com.antsasdomain.medicalapp.dto.pharmacistDTO.PharmacistUpdateDTO;
import com.antsasdomain.medicalapp.model.Pharmacist;
import com.antsasdomain.medicalapp.service.PharmacistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pharmacists")
public class PharmacistController {

    public static final Logger logger = LoggerFactory.getLogger(PharmacistController.class);

    private final PharmacistService pharmacistService;

    public PharmacistController(PharmacistService pharmacistService) {
        this.pharmacistService = pharmacistService;
    }

    @GetMapping
    public ResponseEntity<List<PharmacistResponseDTO>> getAllPharmacists() {
        logger.info("Fetching all the pharmacists");
        ResponseEntity<List<PharmacistResponseDTO>> all = pharmacistService.findAll();
        logger.info("Found all pharmacists");
        return all;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        logger.info("Fetching pharmacist with id {}", id);
        ResponseEntity<?> response = pharmacistService.findById(id);
        logger.info("Fetched pharmacist with id {}", id);
        return response;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PharmacistDTO pharmacistDTO) {
        logger.info("Creating pharmacist {}", pharmacistDTO);
        ResponseEntity<?> response = pharmacistService.save(pharmacistDTO);
        logger.info("Created pharmacist {}", pharmacistDTO);
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        logger.info("Deleting pharmacist with id {}", id);
        ResponseEntity<Map<String, String>> response = pharmacistService.deleteById(id);
        logger.info("Deleted pharmacist with id {}", id);
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody PharmacistUpdateDTO pharmacistDTO) {
        logger.info("Updating pharmacist with id {}", id);

        ResponseEntity<?> update = pharmacistService.update(id, pharmacistDTO);
        logger.info("Updated pharmacist with id {}", id);
        return update;
    }

    @GetMapping("/{id}/prescriptions")
    public ResponseEntity<?> findPrescriptionsByPharmacyCode(@PathVariable Integer id) {
        logger.info("Fetching all prescriptions for pharmacist with id {}", id);
        ResponseEntity<?> response = pharmacistService.findPrescriptionsByPharmacyCode(id);
        logger.info("Found prescriptions for pharmacist with id {}", id);
        return response;
    }

    @PutMapping("/{id}/prescriptions/{prescriptionId}")
    public ResponseEntity<?> fulfillPrescriptionById(
            @PathVariable Integer id,
            @PathVariable Integer prescriptionId) {
        logger.info("Starting to fulfill prescription with ID {} for doctor with ID: {}",
                prescriptionId,
                id);
        ResponseEntity<?> response = pharmacistService.fulfillPrescriptionById(id, prescriptionId);
        logger.info("Finished fulfill prescription with ID {}", prescriptionId);
        return response;
    }
}
