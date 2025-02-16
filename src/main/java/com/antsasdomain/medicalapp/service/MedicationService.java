package com.antsasdomain.medicalapp.service;

import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationDTO;
import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationResponseDTO;
import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationUpdateDTO;
import com.antsasdomain.medicalapp.model.Medication;
import com.antsasdomain.medicalapp.model.MedicationType;
import com.antsasdomain.medicalapp.repository.MedicationRepository;
import com.antsasdomain.medicalapp.repository.PrescriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicationService {

    private final static Logger logger = LoggerFactory.getLogger(MedicationService.class);

    private final MedicationRepository medicationRepository;
    private final PrescriptionRepository prescriptionRepository;

    public MedicationService(MedicationRepository medicationRepository, PrescriptionRepository prescriptionRepository) {
        this.medicationRepository = medicationRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<MedicationResponseDTO> getAllMedications() {
        return medicationRepository.findAll().stream()
                .map(medication -> {
                     return new MedicationResponseDTO(
                            medication.getName(),
                            medication.getDescription(),
                            medication.getDosage(),
                            medication.getMedicationType());
                }).toList();
    }

    public Medication findById(Integer id) {
        return medicationRepository.findById(id).orElse(null);
    }

    public ResponseEntity<?> getMedicationById(Integer id) {
        Optional<Medication> medication = medicationRepository.findById(id);

        if (medication.isPresent()) {
            Medication m = medication.get();
            logger.info("Medication with ID {} found", id);
            var medicationResponseDTO = new MedicationResponseDTO(
                    m.getName(),
                    m.getDescription(),
                    m.getDosage(),
                    m.getMedicationType()
            );
            return ResponseEntity.ok(medicationResponseDTO);
        } else {
            logger.info("Medication with ID {} not found", id);
            return new ResponseEntity<>(
                    Map.of("error", "Medication with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<?> createMedication(MedicationDTO medicationDTO) {
        if (medicationDTO.getMedicationType() == null) { // deserializer handles empty and null
            // values
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error",
                            "Medication Type should be provided. Allowed values: " + Arrays.toString(MedicationType.values())));
        }
        if (MedicationType.NONE.equals(medicationDTO.getMedicationType())) {
            return new ResponseEntity<>
                    (Map.of("error",
                            "Medication Type not allowed. Allowed values: " + getAllMedicationTypes()),
                     HttpStatus.BAD_REQUEST);
        }
        // check first for existence of medication in database:
        Optional<Medication> medicationByName =
                medicationRepository.findByNameIgnoreCase(medicationDTO.getName());

        if (medicationByName.isPresent()) {
            logger.info("Medication with name {} already exists", medicationDTO.getName().toLowerCase());
            return new ResponseEntity<>(
                    Map.of("error", "Medication with name '" + medicationDTO.getName().toLowerCase() + "' " +
                            "already exists. Please provide another medication"),
                    HttpStatus.CONFLICT
            );
        }

        // Transfer data to entity data firstly
        Medication medication = new Medication(
                medicationDTO.getName(),
                medicationDTO.getDescription(),
                medicationDTO.getDosage(),
                medicationDTO.getMedicationType()
        );

        logger.info("Saving medication with ID {}", medication.getId());
        Medication savedMedication = medicationRepository.save(medication);

        // Transfer savedMedication to DTO response
        MedicationResponseDTO medicationResponseDTO = new MedicationResponseDTO(
                savedMedication.getName(),
                savedMedication.getDescription(),
                savedMedication.getDosage(),
                savedMedication.getMedicationType()
        );

        logger.info("Medication with ID {} saved", medication.getId());
        return new ResponseEntity<>(medicationResponseDTO, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateMedication(Medication medication,
                                              MedicationUpdateDTO medicationDTO) {
        if (medicationDTO.getMedicationType() != null) {
            if (MedicationType.NONE.equals(medicationDTO.getMedicationType())) {
                return new ResponseEntity<>
                        (Map.of("error",
                                "Medication Type not allowed. Allowed values: " + getAllMedicationTypes()),
                                HttpStatus.BAD_REQUEST);
            } else {
                medication.setMedicationType(medicationDTO.getMedicationType());
            }
        }
        if (medicationDTO.getName() != null) {
            if (medicationDTO.getName().isEmpty() || medication.getName().isBlank()) {
                return new ResponseEntity<>
                        (Map.of("error", "Name of medicine cannot be empty."),
                         HttpStatus.BAD_REQUEST);
            } else {
                medication.setName(medicationDTO.getName());
            }
        }
        if (medicationDTO.getDescription() != null) {
            if (medicationDTO.getDescription().isEmpty() || medication.getDescription().isBlank()) {
                return new ResponseEntity<>
                        (Map.of("error", "Description of medicine cannot be empty."),
                                HttpStatus.BAD_REQUEST);
            } else {
                medication.setDescription(medicationDTO.getDescription());
            }
        }
        if (medicationDTO.getDosage() != null) {
            if (medicationDTO.getDosage().isEmpty() || medicationDTO.getDosage().isBlank()) {
                return new ResponseEntity<>
                        (Map.of("error", "Dosage of medicine cannot be empty."),
                                HttpStatus.BAD_REQUEST);

            } else {
                medication.setDosage(medicationDTO.getDosage());
            }
        }

        medicationRepository.save(medication);

        // Transfer all data to responseDTO
        MedicationResponseDTO response = new MedicationResponseDTO(
                medication.getName(),
                medication.getDescription(),
                medication.getDosage(),
                medication.getMedicationType()
        );
        logger.info("Medication with ID {} updated", medication.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteMedication(Integer id) {
        logger.info("Deleting medication with ID {}", id);
        Medication medication = medicationRepository.findById(id).orElse(null);
        if (medication == null) {
            logger.error("Medication with ID {} not found", id);
            return new ResponseEntity<>(Map.of("error", "Medication with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        // This verifies if a prescription contains the medicine to delete because if it's still
        // mentioned within a prescription, we cannot delete it in order for there not to be an
        // error in the database.
        Long count = prescriptionRepository.countByMedicineContaining(medication);
        if (count > 0) {
            return new ResponseEntity<>
                    (Map.of("error",
                            "Cannot delete medicine because it's still used in " + count + " " +
                                    "prescription(s)."),
                    HttpStatus.CONFLICT);
        }
        logger.info("Medication with ID {} found. Proceeding with DELETE", id);
        medicationRepository.delete(medication);
        logger.info("Medication with ID {} deleted", id);
        return ResponseEntity.ok().build();
    }

    private String getAllMedicationTypes() {
        return Arrays.stream(MedicationType.values())
                .filter(type -> !type.equals(MedicationType.NONE))
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
