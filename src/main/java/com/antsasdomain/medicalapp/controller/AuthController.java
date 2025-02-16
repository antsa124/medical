package com.antsasdomain.medicalapp.controller;

import com.antsasdomain.medicalapp.dto.auth.LoginRequestDTO;
import com.antsasdomain.medicalapp.dto.auth.RegistrationDTO;
import com.antsasdomain.medicalapp.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * ✅ Register a new user (Doctor, Pharmacist, or Patient)
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDTO registrationDTO) {
        logger.info("Registering user: {}", registrationDTO.getUsername());
        ResponseEntity<?> response = authService.registerNewPerson(registrationDTO);
        logger.info("User registered: {}", response);
        return response;
    }

    /**
     * ✅ Log in a user and return a JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        logger.info("Login user: {}", loginRequestDTO.getUsername());
        ResponseEntity<?> response = authService.loginUser(loginRequestDTO);
        return response;
    }
}
