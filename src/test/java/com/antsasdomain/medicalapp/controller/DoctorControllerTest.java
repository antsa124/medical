package com.antsasdomain.medicalapp.controller;

import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorUpdateDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorWithoutPrescriptionViewResponseDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionForDoctorDTO;
import com.antsasdomain.medicalapp.dto.MedicationDTO;
import com.antsasdomain.medicalapp.model.PrescriptionStatus;
import com.antsasdomain.medicalapp.model.PrescriptionType;
import com.antsasdomain.medicalapp.model.MedicationType;
import com.antsasdomain.medicalapp.service.DoctorService;
import com.antsasdomain.medicalapp.service.PrescriptionService;
import com.antsasdomain.medicalapp.validation.PasswordValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class DoctorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DoctorService doctorService;

    @Mock
    private PrescriptionService prescriptionService;

    @InjectMocks
    private DoctorController doctorController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DoctorDTO doctorDTO;
    private DoctorDTO incorrectDoctorDTO;
    private DoctorUpdateDTO doctorUpdateDTO;
    private DoctorUpdateDTO incorrectDoctorUpdateDTO;
    private PrescriptionForDoctorDTO prescriptionDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule()); // FIX: Enable LocalDate support
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // FIX: Convert LocalDate to "YYYY-MM-DD"
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        doctorDTO = new DoctorDTO(
                "drjohndoe",
                "MyPassword@1!",
                "John",
                "Doe",
                "johndoe@email.com",
                "0123456789",
                "Cardiology Clinic",
                Collections.emptyList()
        );

        incorrectDoctorDTO = new DoctorDTO(
                "drjohndoe",
                "unsecurepassword",
                "John",
                "Doe",
                "johndoe@email.com",
                "123456789",
                "Cardiology Clinic",
                Collections.emptyList()
        );

        doctorUpdateDTO = new DoctorUpdateDTO(
                "drjohndoe",
                "MyP@ssW0rd!111",
                "John",
                "Doe",
                "newemail@email.com",
                "9876543210",
                "Updated Clinic"
        );

        incorrectDoctorUpdateDTO = new DoctorUpdateDTO(
                "drjohndoe",
                "newsecurepassword",
                "John",
                "Doe",
                "newemail@email.com",
                "9876543210",
                "Updated Clinic"
        );

        prescriptionDTO = new PrescriptionForDoctorDTO(
                LocalDate.of(2025, 2, 17),
                PrescriptionType.E_REZEPT,
                List.of(new MedicationDTO(
                        "Ibuprofen", "Pain reliever", "200mg",
                        MedicationType.PILL
                )),
                List.of("QR123456", "QR654321"),
                "PHARM001",
                PrescriptionStatus.ACTIVE
        );
    }

    @Test
    void shouldCreateDoctor() throws Exception {
        when(doctorService.saveDoctor(any(DoctorDTO.class)))
                .thenAnswer(invocation -> ResponseEntity.status(201).body(invocation.getArgument(0)));

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void createDoctor_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incorrectDoctorDTO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldUpdateDoctorById() throws Exception {
        when(doctorService.update(Mockito.eq(1), any(DoctorUpdateDTO.class)))
                .thenAnswer(invocation -> ResponseEntity.ok(invocation.getArgument(1)));

        mockMvc.perform(put("/api/doctors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorUpdateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void updateDoctorById_ShouldReturnBadRequest() throws Exception {
        when(doctorService.update(Mockito.eq(1), any(DoctorUpdateDTO.class)))
                .thenAnswer(invocation -> {
                    DoctorUpdateDTO doctorDTO = invocation.getArgument(1);
                    if (!PasswordValidator.isValid(doctorDTO.getPassword())) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Password is invalid. Should be at least 8 characters " +
                                        "long, contain at least one number, one uppercase letter, and one special character."));
                    }
                    return ResponseEntity.ok(doctorDTO);
                });

        mockMvc.perform(put("/api/doctors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incorrectDoctorUpdateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Password is invalid. Should be at least 8 characters " +
                        "long, contain at least one number, one uppercase letter, and one special character."));
    }


    @Test
    void shouldDeleteDoctorById() throws Exception {
        when(doctorService.deleteById(1))
                .thenAnswer(invocation -> ResponseEntity.noContent().build());

        mockMvc.perform(delete("/api/doctors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldCreatePrescription() throws Exception {
        when(prescriptionService.save(any(PrescriptionForDoctorDTO.class), Mockito.eq(1), Mockito.eq(1)))
                .thenAnswer(invocation -> ResponseEntity.status(201).body(invocation.getArgument(0)));

        mockMvc.perform(post("/api/doctors/1/prescriptions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prescriptionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prescriptionDate").value("2025-02-17"))
                .andExpect(jsonPath("$.prescriptionType").value("E_REZEPT"))
                .andExpect(jsonPath("$.medicine[0].name").value("Ibuprofen"))
                .andExpect(jsonPath("$.medicine[0].description").value("Pain reliever"))
                .andExpect(jsonPath("$.medicine[0].dosage").value("200mg"))
                .andExpect(jsonPath("$.medicine[0].medicationType").value("PILL"))
                .andExpect(jsonPath("$.qrCodes[0]").value("QR123456"))
                .andExpect(jsonPath("$.pharmacyCode").value("PHARM001"))
                .andExpect(jsonPath("$.prescriptionStatus").value("ACTIVE"));
    }
}
