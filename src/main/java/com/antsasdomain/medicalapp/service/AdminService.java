package com.antsasdomain.medicalapp.service;

import com.antsasdomain.medicalapp.dto.auth.RegistrationDTO;
import com.antsasdomain.medicalapp.model.Admin;
import com.antsasdomain.medicalapp.model.AdminLevel;
import com.antsasdomain.medicalapp.model.Person;
import com.antsasdomain.medicalapp.model.PersonType;
import com.antsasdomain.medicalapp.repository.AdminRepository;
import com.antsasdomain.medicalapp.repository.PersonRepository;
import com.antsasdomain.medicalapp.validation.EmailValidator;
import com.antsasdomain.medicalapp.validation.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    private final static Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final PersonRepository personRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(PersonRepository personRepository,
                        AdminRepository adminRepository,
                        PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> approveUser(String username) {
        logger.info("Approving user with username: {}", username);
        Optional<Person> personByUsername = personRepository.findByUsername(username);
        if (personByUsername.isEmpty()) {
            logger.warn("User with username: {} does not exist", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User with username " + username + " not found"));
        }

        Person person = personByUsername.get();
        if (person.isApproved()) {
            logger.warn("User with username: {} is already approved", username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "User is already approved."));
        }

        person.setApproved(true);
        personRepository.save(person);
        logger.info("User with username: '{}' approved", username);
        return ResponseEntity.ok(Map.of("message", "User approved successfully"));
    }


    public ResponseEntity<?> registerAdmin(RegistrationDTO registrationDTO) {
        logger.info("Attempting to register admin");
        // validate person class first
        ResponseEntity<?> responseEntity = validatePerson(registrationDTO);
        if (responseEntity != null) {
            logger.error("Error while registering admin: {}", responseEntity.getBody());
            return responseEntity;
        }

        if (!EmailValidator.isValid(registrationDTO.getEmail())) {
            logger.info("Invalid email address: {}", registrationDTO.getEmail());
            return createErrorResponse("Invalid email.");
        }

        if (PersonType.NONE.equals(registrationDTO.getPersonType())) {
            logger.info("Invalid person type: {}", registrationDTO.getPersonType());
            return createErrorResponse("Person type not supported");
        }
        if (PersonType.SUPER_ADMIN.equals(registrationDTO.getPersonType())
        || PersonType.MODERATOR.equals(registrationDTO.getPersonType())) {
            if (registrationDTO.getAdminLevel() == null
                    || AdminLevel.NONE.equals(registrationDTO.getAdminLevel())) {
                logger.warn("Invalid admin level: {}", registrationDTO.getAdminLevel());
                return createErrorResponse("You need to specify an admin level between " +
                        "SUPER_ADMIN or MODERATOR.");
            }
        }

        if (PersonType.DOCTOR.equals(registrationDTO.getPersonType())
         || PersonType.PATIENT.equals(registrationDTO.getPersonType())
        || PersonType.PHARMACIST.equals(registrationDTO.getPersonType())) {
            logger.info("Person type should be only between SUPER_ADMIN and MODERATOR");
            return createErrorResponse("Person type not supported. Please choose between " +
                    "'SUPER_ADMIN' and 'MODERATOR'.");
        }

        if (personRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            logger.warn("Registration failed: Username '{}' already exists", registrationDTO.getUsername());
            return createErrorResponse("Username already exists. Please choose another.");
        }
        if (personRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            logger.warn("Registration failed: Email '{}' already exists", registrationDTO.getEmail());
            return createErrorResponse("Email already exists. Please choose another.");
        }

        String hashedPassword = passwordEncoder.encode(registrationDTO.getPassword());

        Admin admin = new Admin(
                registrationDTO.getUsername(),
                hashedPassword,
                registrationDTO.getFirstName(),
                registrationDTO.getLastName(),
                registrationDTO.getEmail(),
                registrationDTO.getPhone(),
                registrationDTO.getAdminLevel()
        );

        admin.setPersonType(
                registrationDTO.getAdminLevel() == AdminLevel.SUPER_ADMIN ?
                        PersonType.SUPER_ADMIN :
                        PersonType.MODERATOR
        );

        Admin save = adminRepository.save(admin);
        logger.info("Successfully registered admin");

        return new ResponseEntity<>(Map.of("success", "New admin succesfully registered."),
                HttpStatus.CREATED);
    }

    public ResponseEntity<?> deleteAdmin(Integer id) {
        logger.info("Attempting to delete admin");
        Optional<Admin> byId = adminRepository.findById(id);
        if (byId.isPresent()) {
            adminRepository.delete(byId.get());
            logger.info("Successfully deleted admin");
            return new ResponseEntity<>(Map.of("success", "Admin successfully deleted"), HttpStatus.OK);
        } else {
            logger.warn("Admin with id {} not found", id);
            return createErrorResponse("Admin with id " + id + " not found.");
        }
    }


    private ResponseEntity<?> validatePerson(RegistrationDTO registrationDTO) {
        logger.info("Attempting to validate person");
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
        logger.info("Successfully validated person");
        return null;
    }

    /**
     * ✅ Helper method to create a structured success response.
     */
    private ResponseEntity<?> createSuccessResponse(String message) {
        return ResponseEntity.ok(Map.of("success", message));
    }

    /**
     * ✅ Helper method to create a structured error response.
     */
    private ResponseEntity<?> createErrorResponse(String errorMessage) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errorMessage));
    }
}
