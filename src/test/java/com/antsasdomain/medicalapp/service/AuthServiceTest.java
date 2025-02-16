
package com.antsasdomain.medicalapp.service;

import com.antsasdomain.medicalapp.dto.auth.LoginRequestDTO;
import com.antsasdomain.medicalapp.dto.auth.RegistrationDTO;
import com.antsasdomain.medicalapp.model.*;
import com.antsasdomain.medicalapp.repository.DoctorRepository;
import com.antsasdomain.medicalapp.repository.PatientRepository;
import com.antsasdomain.medicalapp.repository.PersonRepository;
import com.antsasdomain.medicalapp.repository.PharmacistRepository;
import com.antsasdomain.medicalapp.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private RegistrationDTO registrationDTO;
    private Person samplePerson;
    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("newDoctor");
        registrationDTO.setPassword("StrongPass1!");
        registrationDTO.setFirstName("John");
        registrationDTO.setLastName("Doe");
        registrationDTO.setEmail("john.doe@email.com");
        registrationDTO.setPhone("1234567890");
        registrationDTO.setPersonType(PersonType.DOCTOR);
        registrationDTO.setOfficeName("Health Clinic");

        samplePerson = new Doctor();
        samplePerson.setId(1);
        samplePerson.setUsername("newDoctor");
        samplePerson.setPassword("encodedPassword");
        samplePerson.setFirstName("John");
        samplePerson.setLastName("Doe");
        samplePerson.setEmail("john.doe@email.com");
        samplePerson.setPhone("1234567890");
        samplePerson.setPersonType(PersonType.DOCTOR);
        samplePerson.setApproved(true);

        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("newDoctor");
        loginRequestDTO.setPassword("StrongPass1!");
    }

    @Test
    void registerNewPerson_ShouldRegisterDoctorSuccessfully() {
        when(personRepository.findByUsername(registrationDTO.getUsername())).thenReturn(Optional.empty());
        when(personRepository.findByEmail(registrationDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationDTO.getPassword())).thenReturn("hashedPassword");
        when(doctorRepository.save(any(Doctor.class))).thenReturn((Doctor) samplePerson);

        ResponseEntity<?> response = authService.registerNewPerson(registrationDTO);

        assertEquals(200, response.getStatusCode().value());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
        verify(personRepository, times(1)).findByUsername(registrationDTO.getUsername());
        verify(personRepository, times(1)).findByEmail(registrationDTO.getEmail());
        verify(passwordEncoder, times(1)).encode(registrationDTO.getPassword());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void registerNewPerson_ShouldReturnErrorIfUsernameExists() {
        when(personRepository.findByUsername(registrationDTO.getUsername())).thenReturn(Optional.of(samplePerson));
        ResponseEntity<?> response = authService.registerNewPerson(registrationDTO);

        assertEquals(400, response.getStatusCode().value());
        verify(personRepository, never()).findByEmail(anyString());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void registerNewPerson_ShouldReturnErrorIfEmailExists() {
        when(personRepository.findByUsername(registrationDTO.getUsername())).thenReturn(Optional.empty());
        when(personRepository.findByEmail(registrationDTO.getEmail())).thenReturn(Optional.of(samplePerson));

        ResponseEntity<?> response = authService.registerNewPerson(registrationDTO);

        assertEquals(400, response.getStatusCode().value());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void registerNewPerson_ShouldReturnErrorIfAdminTriesToRegister() {
        registrationDTO.setPersonType(PersonType.SUPER_ADMIN);

        ResponseEntity<?> response = authService.registerNewPerson(registrationDTO);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(Map.of("error", "Admins cannot register here."), response.getBody());

        verify(personRepository, never()).findByUsername(anyString());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void loginUser_ShouldAuthenticateAndReturnToken() {
        when(personRepository.findByUsername(loginRequestDTO.getUsername())).thenReturn(Optional.of(samplePerson));
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateToken(authentication)).thenReturn("fake-jwt-token");

        ResponseEntity<?> response = authService.loginUser(loginRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtils, times(1)).generateToken(authentication);
    }

    @Test
    void loginUser_ShouldReturnErrorIfUserNotFound() {
        when(personRepository.findByUsername(loginRequestDTO.getUsername())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authService.loginUser(loginRequestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error: Username not found!"));

        verify(authenticationManager, never()).authenticate(any());
        verify(jwtUtils, never()).generateToken(any());
    }

    @Test
    void loginUser_ShouldReturnErrorIfAccountNotApproved() {
        samplePerson.setApproved(false);
        when(personRepository.findByUsername(loginRequestDTO.getUsername())).thenReturn(Optional.of(samplePerson));

        ResponseEntity<?> response = authService.loginUser(loginRequestDTO);

        assertEquals(403, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("Your account is not approved yet!"));
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtUtils, never()).generateToken(any());
    }
}
