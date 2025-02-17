package com.antsasdomain.medicalapp.controller;

import com.antsasdomain.medicalapp.dto.auth.LoginRequestDTO;
import com.antsasdomain.medicalapp.dto.auth.RegistrationDTO;
import com.antsasdomain.medicalapp.model.PersonType;
import com.antsasdomain.medicalapp.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RegistrationDTO validRegistration;
    private RegistrationDTO invalidRegistration;
    private LoginRequestDTO validLogin;
    private LoginRequestDTO invalidLogin;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        validRegistration = new RegistrationDTO(
                "newdoctor",
                "StrongP@ssw0rd",
                "John",
                "Doe",
                "johndoe@email.com",
                "0123456789",
                PersonType.DOCTOR,
                "Cardiology Clinic",
                null, null, // pharmacist fields
                null, null, null, // patient fields
                null // admin level
        );

        invalidRegistration = new RegistrationDTO(
                "", // Missing username
                "weakpass", // Weak password
                "John",
                "Doe",
                "invalid-email",
                "123",
                PersonType.DOCTOR,
                "Cardiology Clinic",
                null, null,
                null, null, null,
                null
        );

        validLogin = new LoginRequestDTO("newdoctor", "StrongP@ssw0rd");

        invalidLogin = new LoginRequestDTO("", ""); // Missing fields
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        when(authService.registerNewPerson(any(RegistrationDTO.class)))
                .thenAnswer(invocation -> ResponseEntity.ok().body("{\"success\":\"User registered successfully!\"}"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("User registered successfully!"));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_ForInvalidData() throws Exception {
        when(authService.registerNewPerson(any(RegistrationDTO.class)))
                .thenAnswer(invocation -> ResponseEntity.badRequest().body("{\"error\":\"Invalid registration details\"}"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegistration)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid registration details"));
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        when(authService.loginUser(any(LoginRequestDTO.class)))
                .thenAnswer(invocation -> ResponseEntity.ok().body("{\"token\":\"jwt-token-example\"}"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-example"));
    }

    @Test
    void loginUser_ShouldReturnBadRequest_ForInvalidCredentials() throws Exception {
        when(authService.loginUser(any(LoginRequestDTO.class)))
                .thenAnswer(invocation -> ResponseEntity.badRequest().body("{\"error\":\"Invalid username or password\"}"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }
}
