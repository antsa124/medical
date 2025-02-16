package com.antsasdomain.medicalapp.service;

import com.antsasdomain.medicalapp.dto.address.AddressDTO;
import com.antsasdomain.medicalapp.dto.auth.LoginRequestDTO;
import com.antsasdomain.medicalapp.dto.auth.RegistrationDTO;
import com.antsasdomain.medicalapp.model.*;
import com.antsasdomain.medicalapp.repository.DoctorRepository;
import com.antsasdomain.medicalapp.repository.PatientRepository;
import com.antsasdomain.medicalapp.repository.PersonRepository;
import com.antsasdomain.medicalapp.repository.PharmacistRepository;
import com.antsasdomain.medicalapp.utils.JwtUtils;
import com.antsasdomain.medicalapp.validation.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final PersonRepository personRepository;
    private final DoctorRepository doctorRepository;
    private final PharmacistRepository pharmacistRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(
            PersonRepository personRepository,
            DoctorRepository doctorRepository,
            PharmacistRepository pharmacistRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils) {
        this.personRepository = personRepository;
        this.doctorRepository = doctorRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    private ResponseEntity<?> validateRegistration(RegistrationDTO registrationDTO) {

        ResponseEntity<?> responseEntity = validatePerson(registrationDTO);
        if (responseEntity != null) {
            return responseEntity;
        }

        return switch(registrationDTO.getPersonType()) {
            case DOCTOR -> validateDoctor(registrationDTO);
            case PHARMACIST -> validatePharmacist(registrationDTO);
            case PATIENT -> validatePatient(registrationDTO);
            case SUPER_ADMIN -> validateAdmin();
            case MODERATOR -> validateAdmin();
            default -> createErrorResponse("Invalid person type. Choose one between DOCTOR, PHARMACIST, or PATIENT.");
        };
    }

    private ResponseEntity<?> validateAdmin() {
       return createErrorResponse("Admins cannot register here.");
    }

    private ResponseEntity<?> validatePatient(RegistrationDTO registrationDTO) {
        if (registrationDTO.getAddress().getStreet() == null) {
            return createErrorResponse("Address Street is required for person type PATIENT");
        }
        if (registrationDTO.getAddress().getCity() == null) {
            return createErrorResponse("Address City is required for person type PATIENT");
        }
        if (registrationDTO.getAddress().getState() == null) {
            return createErrorResponse("Address State is required for person type PATIENT");
        }
        if (registrationDTO.getAddress().getCountry() == null) {
            return createErrorResponse("Address Country is required for person type PATIENT");
        }
        return null;
    }

    private ResponseEntity<?> validateDoctor(RegistrationDTO registrationDTO) {
        if (registrationDTO.getOfficeName() == null || registrationDTO.getOfficeName().isEmpty()) {
            return createErrorResponse("Office name is required for person type DOCTOR");
        }
        return null;
    }

    private ResponseEntity<?> validatePharmacist(RegistrationDTO registrationDTO) {
        if (registrationDTO.getPharmacyName() == null || registrationDTO.getPharmacyName().isBlank()) {
            return createErrorResponse("PharmacyName is required for person type PHARMACIST");
        }
        if (registrationDTO.getPharmacyCode() == null || registrationDTO.getPharmacyCode().isBlank()) {
            return createErrorResponse("Pharmacy code is required for person type PHARMACIST");
        }
        return null;
    }

    private ResponseEntity<?> validatePerson(RegistrationDTO registrationDTO) {
        if (registrationDTO.getUsername() == null || registrationDTO.getUsername().isBlank()) {
            return createErrorResponse("Username is required");
        }
        if (registrationDTO.getPassword() == null || registrationDTO.getPassword().isBlank()) {
            return createErrorResponse("Password is required");
        }
        if (registrationDTO.getFirstName() == null || registrationDTO.getFirstName().isBlank()) {
            return createErrorResponse("First name is required");
        }
        if (registrationDTO.getLastName() == null || registrationDTO.getLastName().isBlank()) {
            return createErrorResponse("Last name is required");
        }
        if (registrationDTO.getEmail() == null || registrationDTO.getEmail().isBlank()) {
            return createErrorResponse("Email is required");
        }
        if (registrationDTO.getPhone() == null || registrationDTO.getPhone().isBlank()) {
            return createErrorResponse("Phone is required");
        }
        if (registrationDTO.getPersonType() == null) {
            return createErrorResponse("PersonType is required");
        }

        if (!PasswordValidator.isValid(registrationDTO.getPassword())) {
            return createErrorResponse("Password is invalid. Password should contain" +
                    "at least 8 characthers, have at least an uppercas letter, a digit and one " +
                    "special character.");
        }
        return null;
    }
    /**
     * ✅ Registers a new user (Doctor, Pharmacist, or Patient)
     */
    public ResponseEntity<?> registerNewPerson(RegistrationDTO registrationDTO) {
        logger.info("Attempting to register new user: {}", registrationDTO.getUsername());

        ResponseEntity<?> responseEntity = validateRegistration(registrationDTO);
        if (responseEntity != null) {
            logger.error("Validation failed for user: {}", registrationDTO);
            return responseEntity;
        }

        if (personRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            logger.error("Registration failed: Username '{}' already exists",
                    registrationDTO.getUsername());
            return createErrorResponse("Username already exists. Please choose another.");
        }
        if (personRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            logger.error("Registration failed: Email '{}' already exists",
                    registrationDTO.getEmail());
            return createErrorResponse("Email already exists. Please choose another.");
        }

        // Hash password before saving
        String hashedPassword = passwordEncoder.encode(registrationDTO.getPassword());

        if (PersonType.DOCTOR.equals(registrationDTO.getPersonType())) {
            logger.info("Starting registration for doctor");
            return registerDoctor(registrationDTO, hashedPassword);
        }
        if (PersonType.PHARMACIST.equals(registrationDTO.getPersonType())) {
            logger.info("Starting registration for pharmacist");
            return registerPharmacist(registrationDTO, hashedPassword);
        }
        if (PersonType.PATIENT.equals(registrationDTO.getPersonType())) {
            logger.info("Starting registration for patient");
            return registerPatient(registrationDTO, hashedPassword);
        }
        return null;
    }


    private ResponseEntity<?> validateLogin(LoginRequestDTO loginRequestDTO) {
        if (loginRequestDTO.getUsername() == null || loginRequestDTO.getUsername().isBlank()) {
            logger.error("Username is required");
            return createErrorResponse("Username is required.");
        }
        if (loginRequestDTO.getPassword() == null || loginRequestDTO.getPassword().isBlank()) {
            logger.error("Password is required");
            return createErrorResponse("Password is required.");
        }
        return null;
    }

    public ResponseEntity<?> loginUser(LoginRequestDTO loginRequestDTO) {
        logger.info("Attempting to login user: {}", loginRequestDTO.getUsername());
        ResponseEntity<?> responseEntity = validateLogin(loginRequestDTO);
        if (responseEntity != null) {
            logger.error("Validation failed with response body: {}", responseEntity.getBody());
            return responseEntity;
        }
        // Find user by username
        Optional<Person> personOpt = personRepository.findByUsername(loginRequestDTO.getUsername());

        if (personOpt.isEmpty()) {
            logger.error("Username '{}' is not found in the database",
                    loginRequestDTO.getUsername());
            return createErrorResponse("Error: Username not found!");
        }

        Person person = personOpt.get();

        // Check if the account is approved before logging in
        if (!person.isApproved()) {
            logger.warn("Username '{}' is not yet approved", loginRequestDTO.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Your account is not approved yet!"));
        }

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtUtils.generateToken(authentication);

        logger.info("Login successful for user: {}", loginRequestDTO.getUsername());
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }

    private ResponseEntity<?> registerDoctor(RegistrationDTO registrationDTO, String hashedPassword) {
        logger.info("Attempting to register doctor: {}", registrationDTO.getUsername());
        if (registrationDTO.getOfficeName() == null) {
            logger.error("Office name is required");
            return createErrorResponse("Missing required field 'officeName' for doctors.");
        }
        Doctor doctor = new Doctor(
                registrationDTO.getUsername(),
                hashedPassword,
                registrationDTO.getFirstName(),
                registrationDTO.getLastName(),
                registrationDTO.getEmail(),
                registrationDTO.getPhone(),
                registrationDTO.getOfficeName()
        );
        doctorRepository.save(doctor);
        logger.info("Successfuly registered doctor: {}", registrationDTO.getUsername());
        return createSuccessResponse("Doctor registered successfully! Awaiting admin approval.");
    }

    private ResponseEntity<?> registerPharmacist(RegistrationDTO registrationDTO, String hashedPassword) {
        logger.info("Attempting to register pharmacist: {}", registrationDTO.getUsername());
        if (registrationDTO.getPharmacyName() == null || registrationDTO.getPharmacyCode() == null) {
            logger.error("Pharmacist name is required");
            return createErrorResponse("Missing required fields 'pharmacyName' or 'pharmacyCode' for pharmacists.");
        }

        Pharmacist pharmacist = new Pharmacist(
                registrationDTO.getUsername(),
                hashedPassword,
                registrationDTO.getFirstName(),
                registrationDTO.getLastName(),
                registrationDTO.getEmail(),
                registrationDTO.getPhone(),
                registrationDTO.getPharmacyName(),
                registrationDTO.getPharmacyCode()
        );
        pharmacistRepository.save(pharmacist);
        logger.info("Successfuly registered pharmacist: {}", registrationDTO.getUsername());
        return createSuccessResponse("Pharmacist registered successfully! Awaiting admin approval.");
    }

    /**
     * ✅ Handles Patient Registration
     */
    private ResponseEntity<?> registerPatient(RegistrationDTO registrationDTO, String hashedPassword) {
        logger.info("Attempting to register patient: {}", registrationDTO.getUsername());
        if (registrationDTO.getAddress() == null || registrationDTO.getBirthday() == null
                || registrationDTO.getPatientInsuranceNumber() == null) {
            logger.error("Address, birthday and patientInsuranceNumber are required");
            return createErrorResponse("Missing required patient details: address, birthday, or insurance number.");
        }

        AddressDTO addressDTO = registrationDTO.getAddress();
        Address patientAddress = new Address(
                addressDTO.getStreet(),
                addressDTO.getCity(),
                addressDTO.getState(),
                addressDTO.getZipCode(),
                addressDTO.getCountry()
        );

        Patient patient = new Patient(
                registrationDTO.getUsername(),
                hashedPassword,
                registrationDTO.getFirstName(),
                registrationDTO.getLastName(),
                registrationDTO.getEmail(),
                registrationDTO.getPhone(),
                patientAddress,
                registrationDTO.getBirthday(),
                registrationDTO.getPatientInsuranceNumber());
        patient.setApproved(true); // Patients are automatically approved
        patientRepository.save(patient);
        return createSuccessResponse("Patient registered successfully!");
    }

    // helper method
    private ResponseEntity<?> createSuccessResponse(String message) {
        return ResponseEntity.ok(Map.of("success", message));
    }

    // helper method
    private ResponseEntity<?> createErrorResponse(String errorMessage) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errorMessage));
    }
}
