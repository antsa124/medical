package com.antsasdomain.medicalapp.service;

import com.antsasdomain.medicalapp.dto.address.AddressDTO;
import com.antsasdomain.medicalapp.dto.address.AddressResponseDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorResponseDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorUpdateDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorWithoutPrescriptionViewResponseDTO;
import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationResponseDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientResponseDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionForDoctorResponseDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionResponseDTO;
import com.antsasdomain.medicalapp.model.*;
import com.antsasdomain.medicalapp.repository.DoctorRepository;
import com.antsasdomain.medicalapp.repository.PrescriptionRepository;
import com.antsasdomain.medicalapp.validation.EmailValidator;
import com.antsasdomain.medicalapp.validation.PasswordValidator;
import com.antsasdomain.medicalapp.validation.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;
    private final PrescriptionRepository prescriptionRepository;


    public DoctorService(
            DoctorRepository doctorRepository,
            PrescriptionRepository prescriptionRepository) {
        this.doctorRepository = doctorRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<DoctorWithoutPrescriptionViewResponseDTO> findAll() {
        logger.info("Find all doctors");
        return doctorRepository.findAll().stream()
                .map(doctor -> new DoctorWithoutPrescriptionViewResponseDTO(
                        doctor.getUsername(),
                        doctor.getFirstName(),
                        doctor.getLastName(),
                        doctor.getEmail(),
                        doctor.getPhone(),
                        doctor.getOfficeName()
                ))
                .collect(Collectors.toList());
    }

    public DoctorWithoutPrescriptionViewResponseDTO findById(Integer id) {
        logger.info("Find doctor by id: {}", id);
        Optional<Doctor> doctor = doctorRepository.findById(id);

        return doctor.map(this::getNewDoctorResponse).orElse(null);
    }

    private ResponseEntity<?> mapNotEmpty(String param) {
        return new ResponseEntity<>
                (Map.of("error", param + " cannot be blank"),
                        HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> validateDoctor(DoctorDTO doctor) {
        logger.info("Validate doctor: {}", doctor);
        if (doctor.getUsername().isBlank()) {
            return mapNotEmpty("Username");
        }

        if (doctor.getPassword().isBlank()) {
            return mapNotEmpty("Password");
        }

        if (!PasswordValidator.isValid(doctor.getPassword())) {
            return new ResponseEntity<>
                    (Map.of("error", "Password should be  at least 8 characters long, contain one" +
                            " uppercase letter, contain at least one number and at least one " +
                            "special character."),
                            HttpStatus.BAD_REQUEST);
        }

        if (doctor.getFirstName().isBlank()) {
            return mapNotEmpty("Firstname");
        }

        if (doctor.getLastName().isBlank()) {
            return mapNotEmpty("Lastname");
        }

        if (doctor.getEmail().isBlank()) {
            return mapNotEmpty("E-mail");
        }

        if (!EmailValidator.isValid(doctor.getEmail())) {
            return new ResponseEntity<>
                    (Map.of("error", "E-mail is not valid."),
                            HttpStatus.BAD_REQUEST);
        }

        if (doctor.getPhone().isBlank()) {
            return mapNotEmpty("Phone");
        }

        if (!PhoneValidator.isValid(doctor.getPhone())) {
            return new ResponseEntity<>
                    (Map.of("error", "phone should contain between 10 and 15 digits."),
                            HttpStatus.BAD_REQUEST);
        }

        if (doctor.getOfficeName().isBlank()) {
            return mapNotEmpty("Office Name");
        }

        return null;
    }

    private Doctor mapToDoctor(DoctorDTO doctor) {
        logger.info("Starting mapping doctor: {}", doctor);
        Doctor doctorEntity = new Doctor();

        doctorEntity.setPassword(doctor.getPassword());
        doctorEntity.setUsername(doctor.getUsername());
        doctorEntity.setPersonType(PersonType.DOCTOR);
        doctorEntity.setFirstName(doctor.getFirstName());
        doctorEntity.setLastName(doctor.getLastName());
        doctorEntity.setEmail(doctor.getEmail());
        doctorEntity.setPhone(doctor.getPhone());
        doctorEntity.setOfficeName(doctor.getOfficeName());
        doctorEntity.setPrescriptions(doctor.getPrescriptions());

        logger.info("Ending mapping doctor: {}", doctor);
        return doctorEntity;
    }


    public ResponseEntity<?> saveDoctor(DoctorDTO doctor) {
        logger.info("Saving doctor: {}", doctor);

        ResponseEntity<?> responseEntity = validateDoctor(doctor);
        if (responseEntity != null) { // validation fail
            logger.error("Validation error: {}", responseEntity.getBody());
            return responseEntity;
        }

        // transfer data to entity data
        Doctor doctorEntity = mapToDoctor(doctor);

        // Search first for already existing username and e-mail
        Optional<Doctor> doctorbyUsername = doctorRepository.findByUsername(doctor.getUsername());
        if (doctorbyUsername.isPresent()) {
            logger.error("Docter with username {} already exists", doctorbyUsername.get().getUsername());
            return new ResponseEntity<>
                    (Map.of("error",
                            "Doctor with username '" + doctorbyUsername.get().getUsername() + "' already exists"),
                            HttpStatus.CONFLICT);
        }
        Optional<Doctor> doctorByEmail = doctorRepository.findByEmail(doctor.getEmail());
        if (doctorByEmail.isPresent()) {
            logger.error("Doctor with email {} already exists", doctorByEmail.get().getEmail());
            return new ResponseEntity<>
                    (Map.of("error",
                            "Doctor with e-mail address '" + doctorByEmail.get().getEmail() + "' " +
                                    "already exists."),
                            HttpStatus.CONFLICT);
        }

        Doctor savedDoctor = doctorRepository.save(doctorEntity);

        logger.info("Successfully saved doctor: {}", savedDoctor);
        // return user without showing password or id
        return ResponseEntity.ok(getNewDoctorResponse(savedDoctor));
    }

    public ResponseEntity<?> deleteById(Integer id) {
        DoctorWithoutPrescriptionViewResponseDTO byId = findById(id);
        if (byId != null) {
            logger.info("Deleting doctor with id {}", id);
            doctorRepository.deleteById(id);
            return new ResponseEntity<>(Map.of("message", "Doctor deleted successfully"),
                    HttpStatus.OK);
        } else {
            logger.error("Doctor with id {} not found", id);
            return new ResponseEntity<>(Map.of("error", "User with ID " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> update(
            Integer id,
            DoctorUpdateDTO doctorDTO) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);

        if (doctorOpt.isEmpty()) {
            return new ResponseEntity<>
                    (Map.of("error", "Doctor is not registered in the database"),
                            HttpStatus.NOT_FOUND);
        }

        Doctor doctorEntity = doctorOpt.get();

        logger.info("Updating doctor with id {}", doctorEntity.getId());
        if (doctorDTO.getUsername() != null) {
            doctorEntity.setUsername(doctorDTO.getUsername());
        }
        if (doctorDTO.getPassword() != null) {
            if (PasswordValidator.isValid(doctorDTO.getPassword())) {
                doctorEntity.setPassword(doctorDTO.getPassword());
            } else {
                return new ResponseEntity<>
                        (Map.of("error", "Password is invalid. Shoud be at least 8 characters " +
                                "long, should contain at least 1 number, 1 upper-case letter and " +
                                "1 special character."),
                                HttpStatus.BAD_REQUEST);
            }
        }
        if (doctorDTO.getFirstName() != null) {
            doctorEntity.setFirstName(doctorDTO.getFirstName());
        }
        if (doctorDTO.getLastName() != null) {
            doctorEntity.setLastName(doctorDTO.getLastName());
        }
        if (doctorDTO.getEmail() != null) {
            Optional<Doctor> byEmail = doctorRepository.findByEmail(doctorDTO.getEmail());
            if (byEmail.isPresent()) {
                return new ResponseEntity<>(
                        Map.of("error", "The e-mail already exists for a doctor. please " +
                                "use another one"),
                        HttpStatus.BAD_REQUEST
                );
            }
            doctorEntity.setEmail(doctorDTO.getEmail());
        }
        if (doctorDTO.getPhone() != null && !doctorDTO.getPhone().isEmpty()) {
            if (PhoneValidator.isValid(doctorDTO.getPhone())) {
                doctorEntity.setPhone(doctorDTO.getPhone());
            } else {
                return new ResponseEntity<>
                        (Map.of("error", "Phone number has an invalid format. It should between " +
                                "10 and 15 digits long."),
                HttpStatus.BAD_REQUEST);
            }
        }
        if (doctorDTO.getOfficeName() != null) {
            doctorEntity.setOfficeName(doctorDTO.getOfficeName());
        }
        if (doctorDTO.getPrescriptions() != null) {
            doctorEntity.setPrescriptions(doctorDTO.getPrescriptions());
        }

        doctorRepository.save(doctorEntity);

        logger.info("Successfully updated doctor with id {}", doctorEntity.getId());
        return ResponseEntity.ok(getNewDoctorResponse(doctorEntity));
    }

    public ResponseEntity<?> getPrescriptionById(Integer doctorId, Integer prescriptionId) {
        logger.info("Getting doctor prescription with id {}", prescriptionId);
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);

        if (doctor.isPresent()) {
            Optional<Prescription> byIdAndDoctor = prescriptionRepository.findByIdAndDoctor(prescriptionId, doctor.get());
            if (byIdAndDoctor.isPresent()) {
                logger.info("Found doctor prescription with id {}", prescriptionId);
                return ResponseEntity.ok(byIdAndDoctor.get());
            } else {
                logger.error("No prescription with ID {} linked to doctor", prescriptionId);
                return new ResponseEntity<>
                        (Map.of("error", "No prescription linked by doctor."), HttpStatus.NOT_FOUND);
            }
        } else {
            logger.error("Doctor with id {} not found", doctorId);
            return new ResponseEntity<>
                    (Map.of("error", "No doctor with ID " + doctorId + " found."),
                     HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> cancelPrescriptionById(Integer doctorId, Integer prescriptionId) {
        logger.info("Starting to cancel prescription by id {}", prescriptionId);
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if (doctor.isPresent()) {
            Optional<Prescription> prescription =
                    prescriptionRepository.findByIdAndDoctor(prescriptionId, doctor.get());
            if (prescription.isPresent()) {
                logger.info("Found prescription with id {}", prescriptionId);
                if (PrescriptionStatus.CANCELED.equals(prescription.get().getPrescriptionStatus())) {
                    logger.info("prescription is already cancelled.");
                    return new ResponseEntity<>
                            (Map.of("error", "Prescription is already cancelled."),
                                    HttpStatus.OK);
                } else {
                    logger.info("Cancelling prescription with id {}", prescriptionId);
                    prescription.get().setPrescriptionStatus(PrescriptionStatus.CANCELED);
                    Prescription savedPrescription = prescriptionRepository.save(prescription.get());
                    logger.info("Successfully cancelled prescription with id {}", prescriptionId);
                    // TODO: mapping of savedPrescription

                    Patient patient = savedPrescription.getPatient();
                    Address address = patient.getAddress();

                    AddressResponseDTO addressDTO = new AddressResponseDTO();

                    addressDTO.setStreet(address.getStreet());
                    addressDTO.setCity(address.getCity());
                    addressDTO.setState(address.getState());
                    addressDTO.setZipCode(address.getZipCode());
                    addressDTO.setCountry(address.getCountry());

                    PatientResponseDTO patientResponseDTO = new PatientResponseDTO();

                    patientResponseDTO.setUsername(patient.getUsername());
                    patientResponseDTO.setFirstName(patient.getFirstName());
                    patientResponseDTO.setLastName(patient.getLastName());
                    patientResponseDTO.setEmail(patient.getEmail());
                    patientResponseDTO.setPhone(patient.getPhone());
                    patientResponseDTO.setAddress(addressDTO);
                    patientResponseDTO.setPatientInsuranceNumber(patient.getPatientInsuranceNumber());
                    patientResponseDTO.setBirthday(patient.getBirthday());

                    DoctorResponseDTO doctorResponseDTO = new DoctorResponseDTO();
                    doctorResponseDTO.setUsername(doctor.get().getUsername());
                    doctorResponseDTO.setFirstName(doctor.get().getFirstName());
                    doctorResponseDTO.setLastName(doctor.get().getLastName());
                    doctorResponseDTO.setEmail(doctor.get().getEmail());
                    doctorResponseDTO.setPhone(doctor.get().getPhone());
                    doctorResponseDTO.setOfficeName(doctor.get().getOfficeName());

                    List<Medication> medicineList = savedPrescription.getMedicine();

                    List<MedicationResponseDTO> medicationResponseList = medicineList.stream()
                            .map(medicine -> {
                                MedicationResponseDTO medicationResponseDTO = new MedicationResponseDTO();
                                medicationResponseDTO.setMedicationType(medicine.getMedicationType());
                                medicationResponseDTO.setName(medicine.getName());
                                medicationResponseDTO.setDescription(medicine.getDescription());
                                medicationResponseDTO.setDosage(medicine.getDosage());
                                return medicationResponseDTO;
                            })
                            .toList();

                    PrescriptionResponseDTO prescriptionResponseDTO = new PrescriptionResponseDTO();

                    prescriptionResponseDTO.setPatient(patientResponseDTO);
                    prescriptionResponseDTO.setDoctor(doctorResponseDTO);
                    prescriptionResponseDTO.setPatientInsuranceNumber(patientResponseDTO.getPatientInsuranceNumber());
                    prescriptionResponseDTO.setPrescriptionDate(savedPrescription.getPrescriptionDate());
                    prescriptionResponseDTO.setPrescriptionType(savedPrescription.getPrescriptionType());
                    prescriptionResponseDTO.setPrescriptionStatus(savedPrescription.getPrescriptionStatus());
                    prescriptionResponseDTO.setMedicine(medicationResponseList);
                    prescriptionResponseDTO.setQrCodes(savedPrescription.getQrCodes());
                    prescriptionResponseDTO.setPharmacyCode(savedPrescription.getPharmacyCode());
                    prescriptionResponseDTO.setPrescriptionStatus(savedPrescription.getPrescriptionStatus());

                    return new ResponseEntity<>(prescriptionResponseDTO, HttpStatus.OK);
                }
            } else {
                logger.error("No prescription linked to doctor found.");
                return new ResponseEntity<>
                        (Map.of("error", "No prescription linked to doctor found."),
                         HttpStatus.NOT_FOUND);
            }
        } else {
            logger.error("Doctor with id {} not found", doctorId);
            return new ResponseEntity<>
                    (Map.of("error", "No doctor with ID " + doctorId + " found."),
                     HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getPrescriptionsByDoctorId(Integer id) {
        logger.info("Getting doctor prescriptions by doctor with id {}", id);
        Optional<Doctor> doctorById = doctorRepository.findById(id);

        if (doctorById.isPresent()) {
            logger.info("Doctor with id {} found", id);
            List<Prescription> prescriptionsByDoctor = prescriptionRepository.findByDoctor(doctorById.get());
            if (!prescriptionsByDoctor.isEmpty()) {
                logger.info("Found prescriptions by doctor with id {}", id);
                logger.info("Starting to map response.");
                List<PrescriptionForDoctorResponseDTO> responseList = prescriptionsByDoctor.stream()
                        .map(prescription -> {

                            Patient patient = prescription.getPatient();
                            Address address = patient.getAddress();

                            AddressResponseDTO addressDTO = mapToAddressResponseDTO(address);

                            PatientResponseDTO userDto = mapToUserResponseDTO(patient, addressDTO);

                            List<MedicationResponseDTO> medicationResponseDTOs = new ArrayList<>();
                            for (Medication medication : prescription.getMedicine()) {
                                MedicationResponseDTO medicationDTO = new MedicationResponseDTO(
                                        medication.getName(),
                                        medication.getDescription(),
                                        medication.getDosage(),
                                        medication.getMedicationType()
                                );
                                medicationResponseDTOs.add(medicationDTO);
                            }

                            return mapToPrescriptionResponseDTO(
                                    userDto, prescription, medicationResponseDTOs);
                        }).toList();

                logger.info("successfully mapped prescriptions by doctor with id {}", id);
                logger.info("Sucessfuly found prescriptions by doctor with id {}", id);
                return new ResponseEntity<>(responseList, HttpStatus.OK);
            } else {
                logger.error("No prescription linked to doctor found.");
                return new ResponseEntity<>(
                        Map.of("message", "No prescriptions found for Doctor with ID: " + id),
                        HttpStatus.NOT_FOUND);
            }

        } else {
            logger.error("Doctor with id {} not found", id);
            return new ResponseEntity<>(Map.of("error", "Doctor with id '" + id + "' does not exists"), HttpStatus.NOT_FOUND);
        }
    }

    private AddressResponseDTO mapToAddressResponseDTO(Address address) {
        return new AddressResponseDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry());
    }

    private PatientResponseDTO mapToUserResponseDTO(Patient patient, AddressResponseDTO addressDTO) {
        return new PatientResponseDTO(
                patient.getUsername(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                addressDTO,
                patient.getBirthday(),
                patient.getPatientInsuranceNumber()
        );
    }

    private PrescriptionForDoctorResponseDTO mapToPrescriptionResponseDTO (
            PatientResponseDTO userDto,
            Prescription prescription,
            List<MedicationResponseDTO> medicationResponseDTOs) {
        return new PrescriptionForDoctorResponseDTO(
                userDto,
                prescription.getPrescriptionDate(),
                prescription.getPrescriptionType(),
                medicationResponseDTOs,
                prescription.getQrCodes(),
                prescription.getPharmacyCode()
        );
    }

    private DoctorWithoutPrescriptionViewResponseDTO getNewDoctorResponse(Doctor doctor) {
        return new DoctorWithoutPrescriptionViewResponseDTO(
                doctor.getUsername(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getOfficeName());
    }

    public Doctor getUserWithId(Integer id) {
        return doctorRepository.findById(id).orElse(null);
    }

}
