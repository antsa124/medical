package com.antsasdomain.medicalapp.service;

import com.antsasdomain.medicalapp.dto.address.AddressResponseDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorWithoutPrescriptionViewResponseDTO;
import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationResponseDTO;
import com.antsasdomain.medicalapp.dto.pharmacistDTO.PharmacistDTO;
import com.antsasdomain.medicalapp.dto.pharmacistDTO.PharmacistResponseDTO;
import com.antsasdomain.medicalapp.dto.pharmacistDTO.PharmacistUpdateDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionResponseForPrescriptionDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientResponseDTO;
import com.antsasdomain.medicalapp.model.*;
import com.antsasdomain.medicalapp.repository.PharmacistRepository;
import com.antsasdomain.medicalapp.repository.PrescriptionRepository;
import com.antsasdomain.medicalapp.validation.EmailValidator;
import com.antsasdomain.medicalapp.validation.PasswordValidator;
import com.antsasdomain.medicalapp.validation.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.antsasdomain.medicalapp.utils.ValidationUtils.mapNotEmpty;

@Service
public class PharmacistService {

    private static final Logger logger = LoggerFactory.getLogger(PharmacistService.class);

    private final PharmacistRepository pharmacistRepository;
    private final PrescriptionRepository prescriptionRepository;

    public PharmacistService(
            PharmacistRepository pharmacistRepository,
            PrescriptionRepository prescriptionRepository) {
        this.pharmacistRepository = pharmacistRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public ResponseEntity<List<PharmacistResponseDTO>> findAll() {
        logger.info("Find all pharmacists entities in database");
        List<Pharmacist> allEntities = pharmacistRepository.findAll();

        List<PharmacistResponseDTO> response = allEntities.stream()
                .map(pharmacist -> {
                    ResponseEntity<PharmacistResponseDTO> responseEntity =
                            mapToResponseDTO(pharmacist);
                    return responseEntity.getBody();
                })
                .toList();

        logger.info("Found {} pharmacists", allEntities.size());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> findById(Integer id) {
        logger.info("Find pharmacist by id {}", id);
        Optional<Pharmacist> entity = pharmacistRepository.findById(id);

        if (entity.isPresent()) {
            logger.info("Found pharmacist with id {}", id);
            return ResponseEntity.ok(mapToResponseDTO(entity.get()));
        } else {
            logger.info("No pharmacist with id {}", id);
            return new ResponseEntity<>
                    (Map.of("error", "No pharmacist with id " + id),
                     HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> save(PharmacistDTO pharmacistDTO) {

        ResponseEntity<?> responseEntity = validatePharmacist(pharmacistDTO);
        if (responseEntity != null) {
            return responseEntity;
        }

        // transfer to entity
        Pharmacist pharmacist = mapToPharmacist(pharmacistDTO);

        // search for existing pharmacy in database
        Optional<Pharmacist> pharmacistByUsername =
                pharmacistRepository.findByUsername(pharmacist.getUsername());

        if (pharmacistByUsername.isPresent()) {
            return new ResponseEntity<>
                    (Map.of("error",
                            "Pharmacist with username '" + pharmacistByUsername.get().getUsername()
                                    + "' already exists"),
                    HttpStatus.CONFLICT);
        }

        Optional<Pharmacist> pharmaByEmail = pharmacistRepository.findByEmail(pharmacist.getEmail());

        if (pharmaByEmail.isPresent()) {
            return new ResponseEntity<>
                    (Map.of("error",
                            "Pharmacist with email '" + pharmaByEmail.get().getEmail()
                                    + "' already exists"),
                            HttpStatus.CONFLICT);
        }

        Pharmacist savedPharmacist = pharmacistRepository.save(pharmacist);

        return new ResponseEntity<>(mapToResponseDTO(savedPharmacist), HttpStatus.CREATED);
    }

    public ResponseEntity<Map<String, String>> deleteById(Integer id) {
        Optional<Pharmacist> pharmacistOpt = pharmacistRepository.findById(id);

        if (pharmacistOpt.isPresent()) {
            pharmacistRepository.deleteById(id);
            return new ResponseEntity<>
                    (Map.of("success", "Pharmacist with ID " + id + " successfully deleted."),
                            HttpStatus.OK);
        } else {
            return new ResponseEntity<>
                    (Map.of("error", "Pharmacist with id " + id + " not found"),
                            HttpStatus.NOT_FOUND);
        }
    }

    public Optional<Pharmacist> find(Integer id) {
        return pharmacistRepository.findById(id);
    }

    public ResponseEntity<?> update(
            Integer id,
            PharmacistUpdateDTO pharmacistDTO) {

        Optional<Pharmacist> pharmacistOpt = pharmacistRepository.findById(id);

        if (pharmacistOpt.isEmpty()) {
            return new ResponseEntity<>
                    (Map.of("error", "Pharmacist not found in database"),
                            HttpStatus.NOT_FOUND);
        }

        Pharmacist entity = pharmacistOpt.get();

        if (pharmacistDTO.getUsername() != null) {
            entity.setUsername(pharmacistDTO.getUsername());
        }
        if (pharmacistDTO.getPassword() != null) {
            entity.setPassword(pharmacistDTO.getPassword());
        }
        if (pharmacistDTO.getFirstName() != null) {
            entity.setFirstName(pharmacistDTO.getFirstName());
        }
        if (pharmacistDTO.getLastName() != null) {
            entity.setLastName(pharmacistDTO.getLastName());
        }
        if (pharmacistDTO.getEmail() != null) {
            entity.setEmail(pharmacistDTO.getEmail());
        }
        if (pharmacistDTO.getPhone() != null && !pharmacistDTO.getPhone().isBlank()) {
            if (!PhoneValidator.isValid(pharmacistDTO.getPhone())) {
                return new ResponseEntity<>(
                        Map.of("error", "Phone pattern is not valid. Should be between 10 " +
                                "and 15 digits."),
                        HttpStatus.BAD_REQUEST
                );
            } else {
                entity.setPharmacyName(pharmacistDTO.getPhone());
            }
        }
        if (pharmacistDTO.getPharmacyName() != null) {
            entity.setPharmacyName(pharmacistDTO.getPharmacyName());
        }
        if (pharmacistDTO.getPharmacyCode() != null) {
            entity.setPharmacyCode(pharmacistDTO.getPharmacyCode());
        }

        pharmacistRepository.save(entity);

        return mapToResponseDTO(entity);
    }

    public ResponseEntity<?> findPrescriptionsByPharmacyCode(Integer id) {
        Optional<Pharmacist> pharmacistById = pharmacistRepository.findById(id);

        if (pharmacistById.isPresent()) {
            List<Prescription> prescriptions =
                    prescriptionRepository
                            .findByPharmacyCode(pharmacistById.get().getPharmacyCode());

            // map to DTO
            List<PrescriptionResponseForPrescriptionDTO> response = prescriptions.stream()
                    .map(this::mapToPrescriptionResponseDTO)
                    .toList();

            return new ResponseEntity<>
                    (response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>
                    (Map.of("error", "No pharmacist with ID " + id + " found"),
                     HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> fulfillPrescriptionById(Integer pharmacistId, Integer prescriptionId) {
        Optional<Pharmacist> pharmacist = pharmacistRepository.findById(pharmacistId);
        if (pharmacist.isPresent()) {
            Optional<Prescription> prescription =
                    prescriptionRepository.findByIdAndPharmacyCode(prescriptionId, pharmacist.get().getPharmacyCode());
            if (prescription.isPresent()) {
                prescription.get().setPrescriptionStatus(PrescriptionStatus.FULFILLED);
                Prescription savedPrescription = prescriptionRepository.save(prescription.get());
                return new ResponseEntity<>
                        (Map.of("status", "Prescription status fulfilled"), HttpStatus.OK);
            }  else {
                return new ResponseEntity<>
                        (Map.of("error",
                                "no prescription under prescription ID " + prescriptionId + " " +
                                        "found."),
                         HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(
                    Map.of("error", "No pharmacist with ID "+ pharmacistId + " found."),
                    HttpStatus.NOT_FOUND
            );
        }
    }


    private ResponseEntity<?> validatePharmacist(PharmacistDTO pharmacistDTO) {
        if (pharmacistDTO.getUsername().isBlank()) {
            return mapNotEmpty("Username");
        }

        if (pharmacistDTO.getPassword().isBlank()) {
            return mapNotEmpty("Password");
        }

        if (!PasswordValidator.isValid(pharmacistDTO.getPassword())) {
            return new ResponseEntity<>
                    (Map.of("error", "Password should be  at least 8 characters long, contain one" +
                            " uppercase letter, contain at least one number and at least one " +
                            "special character."),
                            HttpStatus.BAD_REQUEST);
        }

        if (pharmacistDTO.getFirstName().isBlank()) {
            return mapNotEmpty("Firstname");
        }

        if (pharmacistDTO.getLastName().isBlank()) {
            return mapNotEmpty("Lastname");
        }

        if (pharmacistDTO.getEmail().isBlank()) {
            return mapNotEmpty("E-mail");
        }

        if (!EmailValidator.isValid(pharmacistDTO.getEmail())) {
            return new ResponseEntity<>
                    (Map.of("error", "E-mail is not valid."),
                            HttpStatus.BAD_REQUEST);
        }

        if (pharmacistDTO.getPhone().isBlank()) {
            return mapNotEmpty("Phone");
        }

        if (!PhoneValidator.isValid(pharmacistDTO.getPhone())) {
            return new ResponseEntity<>
                    (Map.of("error", "phone should contain between 10 and 15 digits."),
                            HttpStatus.BAD_REQUEST);
        }

        if (pharmacistDTO.getPharmacyName().isBlank()) {
            return mapNotEmpty("Pharmacy Name");
        }

        if (pharmacistDTO.getPharmacyCode().isBlank()) {
            return mapNotEmpty("Pharmacy Code");
        }

        return null;
    }

    private ResponseEntity<PharmacistResponseDTO> mapToResponseDTO(Pharmacist pharmacist) {
        PharmacistResponseDTO pharmacistResponseDTO = new PharmacistResponseDTO(
                pharmacist.getUsername(),
                pharmacist.getFirstName(),
                pharmacist.getLastName(),
                pharmacist.getEmail(),
                pharmacist.getPhone(),
                pharmacist.getPharmacyName(),
                pharmacist.getPharmacyCode()
        );
        return ResponseEntity.ok(pharmacistResponseDTO);
    }


    private Pharmacist mapToPharmacist(PharmacistDTO pharmacistDTO) {
        return new Pharmacist(
                pharmacistDTO.getUsername(),
                pharmacistDTO.getPassword(),
                pharmacistDTO.getFirstName(),
                pharmacistDTO.getLastName(),
                pharmacistDTO.getEmail(),
                pharmacistDTO.getPhone(),
                pharmacistDTO.getPharmacyName(),
                pharmacistDTO.getPharmacyCode());
    }

    private PrescriptionResponseForPrescriptionDTO mapToPrescriptionResponseDTO(Prescription prescription) {
        return new PrescriptionResponseForPrescriptionDTO(
                mapToUserResponseDTO(prescription.getPatient()),
                mapToDoctorResponseDTO(prescription.getDoctor()),
                prescription.getPrescriptionDate(),
                prescription.getPrescriptionType(),
                mapToMedicineResponseDTO(prescription.getMedicine()),
                prescription.getQrCodes(),
                prescription.getPharmacyCode(),
                prescription.getPrescriptionStatus());
    }

    private List<MedicationResponseDTO> mapToMedicineResponseDTO(List<Medication> medicine) {
        return medicine.stream()
                .map(med_ -> {
                    return new MedicationResponseDTO(
                            med_.getName(),
                            med_.getDescription(),
                            med_.getDosage(),
                            med_.getMedicationType()
                    );
                }).toList();
    }

    private PatientResponseDTO mapToUserResponseDTO(Patient patient) {
        return new PatientResponseDTO(
                patient.getUsername(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                mapToAddressDTO(patient.getAddress()),
                patient.getBirthday(),
                patient.getPatientInsuranceNumber()
        );
    }

    private AddressResponseDTO mapToAddressDTO(Address address) {
        return new AddressResponseDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry()
        );
    }

    private DoctorWithoutPrescriptionViewResponseDTO mapToDoctorResponseDTO(Doctor doctor) {
        return new DoctorWithoutPrescriptionViewResponseDTO(
                doctor.getUsername(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getOfficeName()
        );
    }

}
