package com.antsasdomain.medicalapp.service;

import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorUpdateDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorWithoutPrescriptionViewResponseDTO;
import com.antsasdomain.medicalapp.model.Doctor;
import com.antsasdomain.medicalapp.model.PersonType;
import com.antsasdomain.medicalapp.repository.DoctorRepository;
import com.antsasdomain.medicalapp.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor sampleDoctor;

    @BeforeEach
    void setUp() {
        sampleDoctor = new Doctor();
        sampleDoctor.setId(1);
        sampleDoctor.setUsername("drsmith");
        sampleDoctor.setPassword("StrongPass1!");
        sampleDoctor.setFirstName("John");
        sampleDoctor.setLastName("Smith");
        sampleDoctor.setEmail("drsmith@email.com");
        sampleDoctor.setPhone("1234567890");
        sampleDoctor.setOfficeName("Health Clinic");
        sampleDoctor.setPersonType(PersonType.DOCTOR);
    }

    @Test
    void findAll_ShouldReturnDoctorList() {
        when(doctorRepository.findAll()).thenReturn(Collections.singletonList(sampleDoctor));

        List<DoctorWithoutPrescriptionViewResponseDTO> doctors = doctorService.findAll();

        assertFalse(doctors.isEmpty());
        assertEquals(1, doctors.size());
        assertEquals(sampleDoctor.getUsername(), doctors.get(0).getUsername());
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShoudReturnEmptyList() {
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

        List<DoctorWithoutPrescriptionViewResponseDTO> doctors = doctorService.findAll();

        assertTrue(doctors.isEmpty());
        assertEquals(0, doctors.size());

        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnDoctorIfExists() {
        when(doctorRepository.findById(1)).thenReturn(Optional.of(sampleDoctor));

        DoctorWithoutPrescriptionViewResponseDTO doctor = doctorService.findById(1);

        assertNotNull(doctor);
        assertEquals(sampleDoctor.getUsername(), doctor.getUsername());
        verify(doctorRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldReturnNullIfDoctorDoesNotExist() {
        when(doctorRepository.findById(1)).thenReturn(Optional.empty());

        DoctorWithoutPrescriptionViewResponseDTO doctor = doctorService.findById(1);

        assertNull(doctor);
        verify(doctorRepository, times(1)).findById(1);
    }

    @Test
    void saveDoctor_ShouldSaveDoctorSuccessfully() {
        DoctorDTO doctorDTO = new DoctorDTO(
                "drsmith",
                "StrongPass1!",
                "John",
                "Smith",
                "drsmith@email.com",
                "1234567890",
                "Health Clinic",
                null
        );

        when(doctorRepository.findByUsername(doctorDTO.getUsername())).thenReturn(Optional.empty());
        when(doctorRepository.findByEmail(doctorDTO.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenReturn(sampleDoctor);

        ResponseEntity<?> response = doctorService.saveDoctor(doctorDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void saveDoctor_ShouldReturnErrorIfUsernameExists() {
        DoctorDTO doctorDTO = new DoctorDTO(
                "drsmith",
                "StrongPass1!",
                "John",
                "Smith",
                "drsmith@email.com",
                "1234567890",
                "Health Clinic",
                null
        );

        when(doctorRepository.findByUsername(doctorDTO.getUsername())).thenReturn(Optional.of(sampleDoctor));

        ResponseEntity<?> response = doctorService.saveDoctor(doctorDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void saveDoctor_ShouldReturnErrorIfEmailExists() {
        DoctorDTO doctorDTO = new DoctorDTO(
                "drsmith",
                "StrongPass1!",
                "John",
                "Smith",
                "drsmith@email.com",
                "1234567890",
                "Health Clinic",
                null
        );

        when(doctorRepository.findByUsername(doctorDTO.getUsername())).thenReturn(Optional.empty());
        when(doctorRepository.findByEmail(doctorDTO.getEmail())).thenReturn(Optional.of(sampleDoctor));

        ResponseEntity<?> response = doctorService.saveDoctor(doctorDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void saveDoctor_ShouldReturnErrorIfPhoneIsInvalid() {
        DoctorDTO doctorDTO = new DoctorDTO(
                "drsmith",
                "StrongPass1!",
                "John",
                "Smith",
                "drsmith@email.com",
                "+iihjk1234567890",
                "Health Clinic",
                null
        );

        ResponseEntity<?> response = doctorService.saveDoctor(doctorDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(doctorRepository, never()).save(any(Doctor.class));
        verify(doctorRepository, never()).findByUsername(doctorDTO.getUsername());
        verify(doctorRepository, never()).findByEmail(doctorDTO.getEmail());
    }

    @Test
    void saveDoctor_ShouldReturnErrorIfSomeFieldsAreBlank() {
        DoctorDTO doctorDTO = new DoctorDTO(
                "",
                null,
                "John",
                "Smith",
                "drsmith@email.com",
                "+iihjk1234567890",
                "Health Clinic",
                null
        );

        ResponseEntity<?> response = doctorService.saveDoctor(doctorDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(doctorRepository, never()).save(any(Doctor.class));
        verify(doctorRepository, never()).findByUsername(doctorDTO.getUsername());
        verify(doctorRepository, never()).findByEmail(doctorDTO.getEmail());
    }

    @Test
    void deleteById_ShouldDeleteDoctorIfExists() {
        when(doctorRepository.findById(1)).thenReturn(Optional.of(sampleDoctor));

        ResponseEntity<?> response = doctorService.deleteById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(doctorRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteById_ShouldReturnErrorIfDoctorDoesNotExist() {
        when(doctorRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = doctorService.deleteById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(doctorRepository, never()).deleteById(anyInt());
    }

    @Test
    void updateDoctor_ShouldUpdateSuccessfully() {
        DoctorUpdateDTO updateDTO = new DoctorUpdateDTO();
        updateDTO.setFirstName("Updated Name");

        when(doctorRepository.findById(1)).thenReturn(Optional.of(sampleDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(sampleDoctor);

        ResponseEntity<?> response = doctorService.update(1, updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void updateDoctor_ShouldReturnErrorIfDoctorNotFound() {
        DoctorUpdateDTO updateDTO = new DoctorUpdateDTO();
        updateDTO.setFirstName("Updated Name");

        when(doctorRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = doctorService.update(1, updateDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void updateDoctor_ShouldReturnErrorIfPhoneIsNotBlankButNotValid() {
        DoctorUpdateDTO updateDTO = new DoctorUpdateDTO();
        updateDTO.setPhone("+wero9u43hewr");

        when(doctorRepository.findById(1)).thenReturn(Optional.of(sampleDoctor));

        ResponseEntity<?> response = doctorService.update(1, updateDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }
}
