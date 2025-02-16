package com.antsasdomain.medicalapp.service;

import com.antsasdomain.medicalapp.dto.address.AddressDTO;
import com.antsasdomain.medicalapp.dto.address.AddressResponseDTO;
import com.antsasdomain.medicalapp.dto.address.AddressUpdateDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorWithoutPrescriptionViewResponseDTO;
import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationResponseDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientResponseDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionResponseForUserDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientUpdateDTO;
import com.antsasdomain.medicalapp.model.*;
import com.antsasdomain.medicalapp.repository.PrescriptionRepository;
import com.antsasdomain.medicalapp.repository.PatientRepository;
import com.antsasdomain.medicalapp.validation.EmailValidator;
import com.antsasdomain.medicalapp.validation.PasswordValidator;
import com.antsasdomain.medicalapp.validation.PatientInsuranceNumberValidator;
import com.antsasdomain.medicalapp.validation.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.antsasdomain.medicalapp.utils.ValidationUtils.mapNotEmpty;

@Service
public class PatientService {

    private final static Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;

    public PatientService(
            PatientRepository patientRepository,
            PrescriptionRepository prescriptionRepository) {
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<PatientResponseDTO> getAllUsers() {
        return patientRepository.findAll().stream()
                .map(user -> new PatientResponseDTO(
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhone(),
                        new AddressResponseDTO(
                                user.getAddress().getStreet(),
                                user.getAddress().getCity(),
                                user.getAddress().getState(),
                                user.getAddress().getZipCode(),
                                user.getAddress().getCountry()),
                        user.getBirthday(),
                        user.getPatientInsuranceNumber()
                ))
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> getUserById(Integer id) {
        Optional<Patient> user = patientRepository.findById(id);

        if (user.isPresent()) {
            return getNewUserResponse(user.get());
        } else {
            return new ResponseEntity<>(
                    Map.of("error", "User not found"),
                    HttpStatus.NOT_FOUND);
        }
    }

    public Patient getUserWithId(Integer id) {
        return patientRepository.findById(id).orElse(null);
    }

    private boolean isBirthDateInThePast(LocalDate birthDate) {
        return birthDate.isBefore(LocalDate.now());
    }

    private ResponseEntity<?> validatePatient(PatientDTO patientDTO) {
        // validation
        if (patientDTO.getUsername().isBlank()) {
            return mapNotEmpty("Username");
        }

        if (patientDTO.getPassword().isBlank()) {
            return mapNotEmpty("Password");
        }

        if (!PasswordValidator.isValid(patientDTO.getPassword())) {
            return new ResponseEntity<>
                    (Map.of("error", "Password should be  at least 8 characters long, contain one" +
                            " uppercase letter, contain at least one number and at least one " +
                            "special character."),
                            HttpStatus.BAD_REQUEST);
        }

        if (patientDTO.getFirstName().isBlank()) {
            return mapNotEmpty("Firstname");
        }

        if (patientDTO.getLastName().isBlank()) {
            return mapNotEmpty("Lastname");
        }

        if (patientDTO.getEmail().isBlank()) {
            return mapNotEmpty("E-mail");
        }

        if (!EmailValidator.isValid(patientDTO.getEmail())) {
            return new ResponseEntity<>
                    (Map.of("error", "E-mail is not valid."),
                            HttpStatus.BAD_REQUEST);
        }

        if (patientDTO.getPhone().isBlank()) {
            return mapNotEmpty("Phone");
        }

        if (!PhoneValidator.isValid(patientDTO.getPhone())) {
            return new ResponseEntity<>
                    (Map.of("error", "phone should contain between 10 and 15 digits."),
                            HttpStatus.BAD_REQUEST);
        }

        if (patientDTO.getBirthday() == null) {
            return mapNotEmpty("Birthday");
        }

        if (!isBirthDateInThePast(patientDTO.getBirthday())) {
            return new ResponseEntity<>
                    (Map.of("error", "Birthdate must be in the past."),
                            HttpStatus.BAD_REQUEST);
        }

        if (patientDTO.getPatientInsuranceNumber() == null || patientDTO.getPatientInsuranceNumber().isBlank()) {
            return mapNotEmpty("Patient Insurance Number");
        }

        if (!PatientInsuranceNumberValidator
                .isValidPatientInsuranceNumber(patientDTO.getPatientInsuranceNumber())) {
            return new ResponseEntity<>
                    (Map.of("error", "Patient insurance number invalid. Should start with" +
                            "a letter followed by either 9 or 11 digits."),
                            HttpStatus.BAD_REQUEST);
        }

        if (patientDTO.getAddress() == null) {
            return mapNotEmpty("Address");
        }

        if (patientDTO.getAddress().getStreet() == null) {
            return mapNotEmpty("Street");
        }

        if (patientDTO.getAddress().getCity() == null) {
            return mapNotEmpty("City");
        }

        if (patientDTO.getAddress().getState() == null) {
            return mapNotEmpty("State");
        }

        if (patientDTO.getAddress().getZipCode() == null) {
            return mapNotEmpty("Zip Code");
        }

        if (patientDTO.getAddress().getCountry() == null) {
            return mapNotEmpty("Country");
        }
        return null;
    }

    private Address mapToAddress(PatientDTO patientDTO) {
        return new Address(
                patientDTO.getAddress().getStreet(),
                patientDTO.getAddress().getCity(),
                patientDTO.getAddress().getState(),
                patientDTO.getAddress().getZipCode(),
                patientDTO.getAddress().getCountry()
        );
    }

    private Patient mapToPatient(PatientDTO patientDTO, Address address) {
        return new Patient(patientDTO.getUsername(),
                patientDTO.getPassword(),
                patientDTO.getFirstName(),
                patientDTO.getLastName(),
                patientDTO.getEmail(),
                patientDTO.getPhone(),
                address,
                patientDTO.getBirthday(),
                patientDTO.getPatientInsuranceNumber()
        );
    }

    public ResponseEntity<?> savePatient(PatientDTO patientDTO) {

        ResponseEntity<?> responseEntity = validatePatient(patientDTO);
        if (responseEntity != null) {
            return responseEntity;
        }

        // Transform PatientDTO
        Address address = mapToAddress(patientDTO);

        Patient user = mapToPatient(patientDTO, address);

        // check for existence of username, mail and patient insurance number
        Optional<Patient> userByUsername = patientRepository.findByUsername(user.getUsername());
        if (userByUsername.isPresent()) {
            return new ResponseEntity<>(
                    Map.of("error", "User with username '" + userByUsername.get().getUsername() +
                            "' already exist in the database. Please choose another username."),
                    HttpStatus.CONFLICT
            );
        }

        Optional<Patient> userByEmail = patientRepository.findByEmail(user.getEmail());
        if (userByEmail.isPresent()) {
            return new ResponseEntity<>(
                    Map.of("error", "User with e-mail '" + userByEmail.get().getEmail()
                            + "' already exist in the database. Please choose another e-mail " +
                            "address."),
                    HttpStatus.CONFLICT
            );
        }

        Optional<Patient> byPatientInsuranceNumber = patientRepository.findByPatientInsuranceNumber(user.getPatientInsuranceNumber());
        if (byPatientInsuranceNumber.isPresent()) {
            return new ResponseEntity<>(
                    Map.of("error",
                            "User with insurance number "+ byPatientInsuranceNumber.get().getPatientInsuranceNumber()
                    + " already exists."),
                    HttpStatus.CONFLICT
            );
        }

        Patient userInDB = patientRepository.save(user);

        // return a user without showing id and the password
        return new ResponseEntity<>(getNewUserResponse(userInDB), HttpStatus.CREATED);
    }

    public ResponseEntity<Map<String, String>> deleteUserById(Integer id) {
        Optional<Patient> byId = patientRepository.findById(id);
        if (byId.isPresent()) {
            patientRepository.delete(byId.get());
            return new ResponseEntity<>(
                    Map.of("success", "User successfully deleted."), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    Map.of("error", "No user with id " + id + " found."),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    public ResponseEntity<?> updateUser(Integer id, PatientUpdateDTO patientUpdateDto) {
        Optional<Patient> userOpt = patientRepository.findById(id);

        if (userOpt.isEmpty()) {
            return new ResponseEntity<>
                    (Map.of("error", "User with ID " + id + " does not exist."),
                            HttpStatus.NOT_FOUND);
        }

        Patient user = userOpt.get();

        if (patientUpdateDto.getUsername() != null) {
            user.setUsername(patientUpdateDto.getUsername());
            logger.info("Updating User with username {}", user.getUsername());
        }
        if (patientUpdateDto.getPassword() != null) {
            user.setPassword(patientUpdateDto.getPassword());
            logger.info("Updating User password");
        }
        if (patientUpdateDto.getFirstName() != null) {
            user.setFirstName(patientUpdateDto.getFirstName());
            logger.info("Updating User with first name {}", user.getFirstName());
        }
        if (patientUpdateDto.getLastName() != null) {
            user.setLastName(patientUpdateDto.getLastName());
            logger.info("Updating User with last name {}", user.getLastName());
        }
        if (patientUpdateDto.getEmail() != null) {
            user.setEmail(patientUpdateDto.getEmail());
            logger.info("Updating User with email {}", user.getEmail());
        }
        if (patientUpdateDto.getPhone() != null && !patientUpdateDto.getPhone().isBlank()) {
            if (PhoneValidator.isValid(patientUpdateDto.getPhone())) {
                user.setPhone(patientUpdateDto.getPhone());
                logger.info("Updating User with phone {}", user.getPhone());
            } else {
                return new ResponseEntity<>
                        (Map.of("error", "Invalid phone format. Should contain between 10 to 15 " +
                                "digits."),
                                HttpStatus.BAD_REQUEST);
            }
        }
        if (patientUpdateDto.getAddress() != null) {
            Address address = user.getAddress();
            AddressUpdateDTO addressDTO = patientUpdateDto.getAddress();
            logger.info("Starting updating User address...");

            if (addressDTO.getStreet() != null) {
                address.setStreet(addressDTO.getStreet());
                logger.info("Updating User address with street {}", address.getStreet());
            }
            if (addressDTO.getCity() != null) {
                address.setCity(addressDTO.getCity());
                logger.info("Updating User address with city {}", address.getCity());
            }
            if (addressDTO.getState() != null) {
                address.setState(addressDTO.getState());
                logger.info("Updating User address with state {}", address.getState());
            }
            if (addressDTO.getZipCode() != null) {
                address.setZipCode(addressDTO.getZipCode());
                logger.info("Updating User address with zipCode {}", address.getZipCode());
            }
            if (addressDTO.getCountry() != null) {
                address.setCountry(addressDTO.getCountry());
                logger.info("Updating User address with country {}", address.getCountry());
            }

            user.setAddress(address);
            logger.info("Updating User with address {}", user.getAddress());
        }

        // persist into database
        patientRepository.save(user);

        logger.info("User with ID {} updated successfully", user.getId());
        // return a user without showing id and the password
        return getNewUserResponse(user);
    }

    public List<PrescriptionResponseForUserDTO> findAllPrescriptionsById(Integer id) {
        Optional<Patient> user = patientRepository.findById(id);
        if (user.isPresent()) {
            List<Prescription> prescriptions = prescriptionRepository.findByPatient(user.get());
            return prescriptions.isEmpty() ? Collections.emptyList() :
                    prescriptions.stream().map(this::mapToPrescriptionResponseDTO).toList();
        } else {
            return Collections.emptyList();
        }
    }

    private PatientResponseDTO mapToUserResponseDTO(Patient patient) {
        return new PatientResponseDTO(
                patient.getUsername(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                mapToAddressResponseDTO(patient.getAddress()),
                patient.getBirthday(),
                patient.getPatientInsuranceNumber()
        );
    }

    private AddressDTO mapToAddressDTO(Address address) {
        return new AddressDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry()
        );
    }

    private AddressResponseDTO mapToAddressResponseDTO(Address address) {
        return new AddressResponseDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry()
        );
    }

    private PrescriptionResponseForUserDTO mapToPrescriptionResponseDTO(Prescription prescription) {
        // TODO
        return new PrescriptionResponseForUserDTO(
                mapToDoctorResponseDTO(prescription.getDoctor()),
                prescription.getPrescriptionDate(),
                prescription.getPrescriptionType(),
                mapToMedicineResponseDTO(prescription.getMedicine()),
                prescription.getQrCodes(),
                prescription.getPharmacyCode(),
                prescription.getPrescriptionStatus()
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



    private ResponseEntity<?> getNewUserResponse(Patient user) {
        PatientResponseDTO patientResponseDTO = new PatientResponseDTO(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                new AddressResponseDTO(
                        user.getAddress().getStreet(),
                        user.getAddress().getCity(),
                        user.getAddress().getState(),
                        user.getAddress().getZipCode(),
                        user.getAddress().getCountry()),
                user.getBirthday(),
                user.getPatientInsuranceNumber());

        return new ResponseEntity<>(patientResponseDTO, HttpStatus.CREATED);
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
}
