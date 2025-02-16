package com.antsasdomain.medicalapp.controller;

import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionResponseForPrescriptionDTO;
import com.antsasdomain.medicalapp.service.PrescriptionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionController.class);

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionResponseForPrescriptionDTO>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPrescriptionById(@PathVariable Integer id) {
        logger.info("Starting GET prescription by id {}", id);
        PrescriptionResponseForPrescriptionDTO dto = prescriptionService.getPrescriptionById(id);
        if (dto == null) {
            logger.info("No prescription found with id {}", id);
            return new ResponseEntity<>(Map.of("error", "No prescription with ID " + " found."),
                     HttpStatus.NOT_FOUND);
        } else {
            logger.info("Found prescription with id {}", id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }
}
