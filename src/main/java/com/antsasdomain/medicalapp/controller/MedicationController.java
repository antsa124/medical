package com.antsasdomain.medicalapp.controller;

import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationDTO;
import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationResponseDTO;
import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationUpdateDTO;
import com.antsasdomain.medicalapp.model.Medication;
import com.antsasdomain.medicalapp.service.MedicationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicines")
public class MedicationController {

    private static final Logger logger = LoggerFactory.getLogger(MedicationController.class);

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    public ResponseEntity<List<MedicationResponseDTO>> getAllMedications() {
        logger.info("Fetching all medications");
        ResponseEntity<List<MedicationResponseDTO>> response = ResponseEntity.ok(medicationService.getAllMedications());
        logger.info("Fetched all medications");
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicationById(@PathVariable Integer id) {
        logger.info("Fetching medication with id {}", id);
        ResponseEntity<?> response = medicationService.getMedicationById(id);
        logger.info("Fetched medication with id {}", id);
        return response;
    }

    @PostMapping
    public ResponseEntity<?> createMedication(@Valid @RequestBody MedicationDTO medicationDTO) {
        logger.info("Creating medication {}", medicationDTO);
        ResponseEntity<?> medication = medicationService.createMedication(medicationDTO);
        logger.info("Created medication {}", medication);
        return medication;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedication(@PathVariable Integer id,
                                              @Valid @RequestBody MedicationUpdateDTO medicationDTO) {
        logger.info("Updating medication with id {}", id);
        Medication medication = medicationService.findById(id);

        if (medication != null) {
            return medicationService.updateMedication(medication, medicationDTO);
        } else {
            return new ResponseEntity<>
                    (Map.of("error", "Medication with ID " + id + " not found"),
                     HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedication(@PathVariable Integer id) {
        logger.info("Deleting medication with id {}", id);
        ResponseEntity<?> response = medicationService.deleteMedication(id);
        logger.info("Deleted medication with id {}", id);
        return response;
    }

}
