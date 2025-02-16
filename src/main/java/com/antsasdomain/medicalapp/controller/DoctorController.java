package com.antsasdomain.medicalapp.controller;

import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorUpdateDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorWithoutPrescriptionViewResponseDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionForDoctorDTO;
import com.antsasdomain.medicalapp.model.Doctor;
import com.antsasdomain.medicalapp.model.Patient;
import com.antsasdomain.medicalapp.service.DoctorService;
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
@RequestMapping("/api/doctors")
public class DoctorController {

    public static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    private final DoctorService doctorService;
    private final PrescriptionService prescriptionService;

    public DoctorController(DoctorService doctorService,
                            PrescriptionService prescriptionService) {
        this.doctorService = doctorService;
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorWithoutPrescriptionViewResponseDTO>> getAllDoctors() {
        logger.info("Fetching all the doctors...");
        List<DoctorWithoutPrescriptionViewResponseDTO> doctors = doctorService.findAll();
        logger.info("Found {} doctors", doctors.size());
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Integer id) {
        logger.info("Fetching a doctor by id: {}", id);
        DoctorWithoutPrescriptionViewResponseDTO doctorResponse = doctorService.findById(id);
        if (doctorResponse != null) { // no entity found
            logger.info("Fetched doctor with ID: {}", id);
            return ResponseEntity.ok(doctorResponse);
        } else {
            logger.info("No doctor with ID {} found", id);
            return new ResponseEntity<>(
                    Map.of("error:", "No doctor with ID " + id + " found"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        logger.info("Creating a doctor...");
        ResponseEntity<?> responseEntity = doctorService.saveDoctor(doctorDTO);
        logger.info("Created doctor");
        return responseEntity;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctorById(@PathVariable Integer id) {
        logger.info("Delete doctor by ID: {}", id);
        return doctorService.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDoctorById(@PathVariable Integer id,
                                              @Valid @RequestBody DoctorUpdateDTO doctorDTO) {

        logger.info("Updating Doctor with ID: {}", id);
        ResponseEntity<?> updatedUser = doctorService.update(id, doctorDTO);
        logger.info("Updated Doctor with ID {}", id);
        return updatedUser;
    }

    @GetMapping("/{id}/prescriptions")
    public ResponseEntity<?> getPrescriptionsByDoctorId(@PathVariable Integer id) {
        logger.info("Fetching all prescriptions for doctor with ID: {}", id);
        ResponseEntity<?> response = doctorService.getPrescriptionsByDoctorId(id);
        logger.info("Fetched prescriptions for doctor with ID: {}", id);
        return response;
    }

    @GetMapping("/{doctorId}/prescriptions/{prescriptionId}")
    public ResponseEntity<?> getPrescriptionByIds(@PathVariable Integer doctorId,
                                                  @PathVariable Integer prescriptionId) {
        logger.info("Fetching prescription with ID {} for doctor with ID: {}", prescriptionId, doctorId);
        ResponseEntity<?> prescriptionById = doctorService.getPrescriptionById(doctorId, prescriptionId);
        logger.info("Fetched prescription with ID {}", prescriptionId);
        return prescriptionById;
    }

    @PutMapping("/{id}/prescriptions/{prescriptionId}")
    public ResponseEntity<?> cancelPrescriptionById(
            @PathVariable Integer id,
            @PathVariable Integer prescriptionId) {
        logger.info("Starting cancel prescription with ID {} for doctor with ID: {}", prescriptionId, id);
        ResponseEntity<?> response = doctorService.cancelPrescriptionById(id, prescriptionId);
        logger.info("Finished cancel prescription with ID {}", prescriptionId);
        return response;
    }

    @PostMapping("/{id}/prescriptions/{patientId}")
    public ResponseEntity<?> createPrescription(@RequestBody PrescriptionForDoctorDTO prescriptionDTO,
                                                @PathVariable Integer id,
    @PathVariable Integer patientId) {
        logger.info("Doctor is creating a prescription...");
        ResponseEntity<?> response = prescriptionService.save(prescriptionDTO, id, patientId);
        logger.info("Created prescription with ID {}", id);
        return response;
    }


}
