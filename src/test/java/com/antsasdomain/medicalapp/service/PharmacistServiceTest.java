package com.antsasdomain.medicalapp.service;

import com.antsasdomain.medicalapp.dto.pharmacistDTO.PharmacistDTO;
import com.antsasdomain.medicalapp.dto.pharmacistDTO.PharmacistResponseDTO;
import com.antsasdomain.medicalapp.dto.pharmacistDTO.PharmacistUpdateDTO;
import com.antsasdomain.medicalapp.model.*;
import com.antsasdomain.medicalapp.repository.PharmacistRepository;
import com.antsasdomain.medicalapp.repository.PrescriptionRepository;
import org.hibernate.dialect.function.ListaggFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PharmacistServiceTest {

    @Mock
    private PharmacistRepository pharmacistRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @InjectMocks
    private PharmacistService pharmacistService;

    private Pharmacist pharmacist;
    private PharmacistDTO pharmacistDTO;
    private PharmacistUpdateDTO pharmacistUpdateDTO;
    private Prescription prescription;

    @BeforeEach
    void setUp() {
        pharmacist = new Pharmacist();
        pharmacist.setId(1);
        pharmacist.setUsername("newpharmacist");
        pharmacist.setPassword("StrongP@ss1!");
        pharmacist.setFirstName("John");
        pharmacist.setLastName("Doe");
        pharmacist.setEmail("john.doe@gmail.com");
        pharmacist.setPhone("0123456789");
        pharmacist.setPersonType(PersonType.PHARMACIST);
        pharmacist.setPharmacyName("Hauptbahnhof Apotheke");
        pharmacist.setPharmacyCode("PHARMA002");

        pharmacistDTO = new PharmacistDTO(
                "newpharmacist",
                "StrongP@ss1!",
                "John",
                "Doe",
                "john.doe@gmail.com",
                "0123456789",
                "Hauptbahnhof Apotheke",
                "PHARMA002"
        );

        pharmacistUpdateDTO = new PharmacistUpdateDTO();
        pharmacistUpdateDTO.setUsername("updatedPharmacist");
        pharmacistUpdateDTO.setFirstName("UpdatedName");
        pharmacistUpdateDTO.setLastName("UpdatedLastName");
        pharmacistUpdateDTO.setEmail("updated.email@gmail.com");
        pharmacistUpdateDTO.setPhone("0987654321");

        Address address = new Address();
        address.setStreet("Street 1");
        address.setCity("City");
        address.setState("State");
        address.setZipCode("12345");
        address.setCountry("Country");

        Patient patient = new Patient(
                "patient1",
                "Strong password",
                "Jane",
                "Dae",
                "jane.dae@gmail.com",
                "0123456789",
                address,
                LocalDate.of(1998, 8, 3),
                "G123456789");

        Doctor doctor = new Doctor(
                "newDoctor",
                "StrongP@ss1!",
                "John",
                "Smith",
                "john.smith@email.com",
                "0123456789",
                "John Doe's office"
        );

        Medication medication = new Medication(
                "Aspirin", "against headache", "1 pill per day", MedicationType.PILL
        );


        prescription = new Prescription();
        prescription.setId(101);
        prescription.setPrescriptionStatus(PrescriptionStatus.FULFILLED);
        prescription.setPharmacyCode("PHARMA002");
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setMedicine(List.of(medication));
    }

    @Test
    void testFindAll_ShouldReturnAllPharmacists() {
        when(pharmacistRepository.findAll()).thenReturn(List.of(pharmacist));

        ResponseEntity<List<PharmacistResponseDTO>> response = pharmacistService.findAll();

        assertEquals(1, response.getBody().size());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(pharmacistRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_ShouldReturnEmptyList() {
        when(pharmacistRepository.findAll()).thenReturn(new ArrayList<>());

        ResponseEntity<List<PharmacistResponseDTO>> response = pharmacistService.findAll();

        assertEquals(0, response.getBody().size());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(pharmacistRepository, times(1)).findAll();
    }

    @Test
    void testFindById_ShouldReturnPharmacist() {
        when(pharmacistRepository.findById(1)).thenReturn(Optional.of(pharmacist));

        ResponseEntity<?> response = pharmacistService.findById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(pharmacistRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_ShouldReturnNotFound() {
        when(pharmacistRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = pharmacistService.findById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No pharmacist with id 1"));

        verify(pharmacistRepository, times(1)).findById(1);
    }

    @Test
    void testSave_ShouldSavePharmacist() {
        when(pharmacistRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(pharmacistRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(pharmacistRepository.save(any())).thenReturn(pharmacist);

        ResponseEntity<?> response = pharmacistService.save(pharmacistDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(pharmacistRepository, times(1)).save(any());
        verify(pharmacistRepository, times(1)).findByUsername(anyString());
        verify(pharmacistRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testSave_ShouldReturnErrorIfUsernameExists() {
        when(pharmacistRepository.findByUsername(anyString())).thenReturn(Optional.of(pharmacist));

        ResponseEntity<?> response = pharmacistService.save(pharmacistDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testUpdate_ShouldUpdatePharmacist() {
        when(pharmacistRepository.findById(1)).thenReturn(Optional.of(pharmacist));
        when(pharmacistRepository.save(any())).thenReturn(pharmacist);

        ResponseEntity<?> response = pharmacistService.update(1, pharmacistUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(pharmacistRepository, times(1)).save(any());
    }

    @Test
    void testUpdate_ShouldReturnNotFound() {
        when(pharmacistRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = pharmacistService.update(1, pharmacistUpdateDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(pharmacistRepository, never()).save(any());
    }

    @Test
    void testDelete_ShouldDeletePharmacist() {
        when(pharmacistRepository.findById(1)).thenReturn(Optional.of(pharmacist));

        ResponseEntity<Map<String, String>> response = pharmacistService.deleteById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(pharmacistRepository, times(1)).deleteById(1);
        verify(pharmacistRepository, times(1)).findById(1);
    }

    @Test
    void testDelete_ShouldReturnErrorIfPharmacistNotFound() {
        when(pharmacistRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = pharmacistService.deleteById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(pharmacistRepository, never()).deleteById(1);
        verify(pharmacistRepository, times(1)).findById(1);
    }

    @Test
    void testFindPrescriptionsByPharmacyCode_ShouldReturnPrescriptions() {
        when(pharmacistRepository.findById(1)).thenReturn(Optional.of(pharmacist));
        when(prescriptionRepository.findByPharmacyCode(pharmacist.getPharmacyCode()))
                .thenReturn(List.of(prescription));

        ResponseEntity<?> response = pharmacistService.findPrescriptionsByPharmacyCode(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testFindPrescriptionsByPharmacyCode_ShouldReturnError() {
        when(pharmacistRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = pharmacistService.findPrescriptionsByPharmacyCode(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(prescriptionRepository, never()).findByPharmacyCode(pharmacist.getPharmacyCode());
    }

    @Test
    void testFulfillPrescriptionById_ShouldMarkPrescriptionFulfilled() {
        when(pharmacistRepository.findById(1)).thenReturn(Optional.of(pharmacist));
        when(prescriptionRepository.findByIdAndPharmacyCode(anyInt(), anyString()))
                .thenReturn(Optional.of(prescription));
        when(prescriptionRepository.save(any())).thenReturn(prescription);

        ResponseEntity<?> response = pharmacistService.fulfillPrescriptionById(1, 101);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testFulfillPrescriptionById_ShouldReturnError() {
        when(pharmacistRepository.findById(1)).thenReturn(Optional.of(pharmacist));
        when(prescriptionRepository.findByIdAndPharmacyCode(anyInt(), anyString()))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = pharmacistService.fulfillPrescriptionById(1, 101);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
