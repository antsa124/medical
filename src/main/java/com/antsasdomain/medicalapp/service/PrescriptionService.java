package com.antsasdomain.medicalapp.service;

import com.antsasdomain.medicalapp.dto.address.AddressDTO;
import com.antsasdomain.medicalapp.dto.address.AddressResponseDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorDTO;
import com.antsasdomain.medicalapp.dto.doctorDTO.DoctorWithoutPrescriptionViewResponseDTO;
import com.antsasdomain.medicalapp.dto.medicationDTO.MedicationResponseDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionForDoctorDTO;
import com.antsasdomain.medicalapp.dto.prescriptionDTO.PrescriptionResponseForPrescriptionDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientDTO;
import com.antsasdomain.medicalapp.dto.patientDTO.PatientResponseDTO;
import com.antsasdomain.medicalapp.model.*;
import com.antsasdomain.medicalapp.repository.*;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    private PrescriptionRepository prescriptionRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private MedicationRepository medicationRepository;
    private PharmacistRepository pharmacistRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            MedicationRepository medicationRepository,
            PharmacistRepository pharmacistRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.medicationRepository = medicationRepository;
        this.pharmacistRepository = pharmacistRepository;
    }

    public List<PrescriptionResponseForPrescriptionDTO> getAllPrescriptions() {
        List<Prescription> prescriptions = prescriptionRepository.findAll();
        if (!prescriptions.isEmpty()) {
            return prescriptions.stream().map(this::mapToPrescriptionResponseDTO).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public PrescriptionResponseForPrescriptionDTO getPrescriptionById(Integer id) {
        Optional<Prescription> prescription = prescriptionRepository.findById(id);
        return prescription.map(this::mapToPrescriptionResponseDTO).orElse(null);
    }

    private ResponseEntity<?> mapNotEmpty(String param) {
        return new ResponseEntity<>
                (Map.of("error", param + " cannot be blank"),
                        HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> validateEmpty(PrescriptionForDoctorDTO prescriptionDTO) {
        if (prescriptionDTO.getPrescriptionDate() == null) {
            return mapNotEmpty("PrescriptionDate");
        }
        if (prescriptionDTO.getPrescriptionType() == null) {
            return mapNotEmpty("PrescriptionType");
        }
        if (prescriptionDTO.getMedicine() == null || prescriptionDTO.getMedicine().isEmpty()) {
            return mapNotEmpty("Medicine");
        }
        if (prescriptionDTO.getQrCodes() == null || prescriptionDTO.getQrCodes().isEmpty()) {
            return mapNotEmpty("QrCodes");
        }
        if (prescriptionDTO.getPharmacyCode() == null || prescriptionDTO.getPharmacyCode().isEmpty()) {
            return mapNotEmpty("PharmacyCode");
        }
        if (prescriptionDTO.getPrescriptionStatus() == null) {
            return mapNotEmpty("PrescriptionStatus");
        }
        return null;
    }

    public ResponseEntity<?> save(
            PrescriptionForDoctorDTO prescriptionDTO,
            Integer doctorId,
            Integer patientId) {
        logger.info("Attempting to save prescription for doctor with ID {} and patient ID {}", doctorId, patientId);
        // validate all fields
        ResponseEntity<?> responseEntity = validateEmpty(prescriptionDTO);
        if (responseEntity != null) {
            logger.error("Not all fields are valid: {}", responseEntity.getBody().toString());
            return responseEntity;
        }
        if (PrescriptionType.NONE.equals(prescriptionDTO.getPrescriptionType())){
            logger.error("Prescription type should be E-REZEPT or PRIVAT");
            return new ResponseEntity<>(
                    Map.of("error", "Prescritpion type should be E-REZEPT or PRIVAT"),
                    HttpStatus.BAD_REQUEST
            );
        }
        // only doctors can create prescriptions. And prescriptions can only have the empty
        if (!PrescriptionStatus.ACTIVE.equals(prescriptionDTO.getPrescriptionStatus())) {
            logger.error("Prescription status should be ACTIVE during creation of prescription");
            return new ResponseEntity<>(
                    Map.of("error",
                            "Prescription must have Status ACTIVE during creation."),
                    HttpStatus.BAD_REQUEST
            );
        }

        Prescription prescription = new Prescription();

        // Prescription is child to User, so check that user exists first before persisting
        Optional<Patient> patientOpt =
                patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            logger.info("Patient with ID {} does not exist", patientId);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", " Patient with ID " + patientId + " does not exist."));
        }
        Patient patient = patientOpt.get();

        // Prescription is child to Doctor. So check that doctor exists first before persisting
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            logger.info("Doctor with ID {} does not exist", doctorId);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Doctor with ID " + doctorId + " does not exist."));
        }
        Doctor doctor = doctorOpt.get();

        List<Medication> medicines = mapToMedicine(prescriptionDTO);

        List<String> medicineNames = medicines.stream()
                .map(Medication::getName)
                .toList();

        // check if medications exist in the medication table
        List<Medication> savedMedicine = medicationRepository.findByNameIn(medicineNames);

        // if some medications are not corresponding to the ones in the Medication table
        if (savedMedicine.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Medicines not registered in the database."));
        }

        if (savedMedicine.size() != medicineNames.size()) {
            Set<String> foundMedications = savedMedicine.stream()
                    .map(Medication::getName)
                    .collect(Collectors.toSet());

            Set<String> missingMedications = new HashSet<>(medicineNames);
            missingMedications.removeAll(foundMedications);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)  // Change status to 400 if it's a user input issue
                    .body(Map.of(
                            "error", "Some medications are no longer available. Please refresh and try again.",
                            "missingMedications", missingMedications));
        }

        Optional<Pharmacist> byPharmacyCode = pharmacistRepository.findByPharmacyCode(prescriptionDTO.getPharmacyCode());
        if (byPharmacyCode.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Pharmacy code doesn't exist."
                    ));
        }

        Pharmacist pharmacist = byPharmacyCode.get();

        if (PrescriptionType.NONE.equals(prescriptionDTO.getPrescriptionType())) {
            logger.info("Prescription type cannot be empty, or NONE. Here are the possible values" +
                    " : {}", getAllPrescriptionTypes());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Prescription type not supported. Here are the " +
                            "supported types: " + getAllPrescriptionTypes()));
        }

        if (PrescriptionStatus.NONEXISTENT.equals(prescriptionDTO.getPrescriptionStatus())) {
            logger.error("Prescription status not supported. Here are the supported status: {}",
                    getPrescriptionsStatus());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Prescription status not supported. Here are the " +
                            "supported status: " + getPrescriptionsStatus()));
        }

        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setPrescriptionDate(prescriptionDTO.getPrescriptionDate());
        prescription.setPrescriptionType(prescriptionDTO.getPrescriptionType());
        prescription.setMedicine(savedMedicine);
        prescription.setQrCodes(prescriptionDTO.getQrCodes());
        prescription.setPharmacyCode(pharmacist.getPharmacyCode());
        prescription.setPrescriptionStatus(PrescriptionStatus.ACTIVE); // status is always active
        // when created

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        PrescriptionResponseForPrescriptionDTO responseDTO = mapToResponseDTO(
                savedPrescription,
                mapToUserResponseDTO(patient),
                mapToDoctorResponseDTO(doctor),
                mapToMedicineResponseDTO(savedPrescription.getMedicine())
        );

        logger.info("Successfully saved prescription");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
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

    private PatientResponseDTO mapToUserResponseDTO(Patient patient) {
        return new PatientResponseDTO(
                patient.getUsername(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                mapToAddressDTO(patient.getAddress()),
                patient.getBirthday(),
                patient.getPatientInsuranceNumber()
        );
    }

    private AddressResponseDTO mapToAddressDTO(Address address) {
        return new AddressResponseDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry()
        );
    }

    private PrescriptionResponseForPrescriptionDTO mapToPrescriptionResponseDTO(Prescription prescription) {
        return new PrescriptionResponseForPrescriptionDTO(
                mapToUserResponseDTO(prescription.getPatient()),
                mapToDoctorResponseDTO(prescription.getDoctor()),
                prescription.getPrescriptionDate(),
                prescription.getPrescriptionType(),
                mapToMedicineResponseDTO(prescription.getMedicine()),
                prescription.getQrCodes(),
                prescription.getPharmacyCode(),
                prescription.getPrescriptionStatus());
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

    private Address mapToAddress(AddressDTO addressDTO) {
        return new Address(
                addressDTO.getStreet(),
                addressDTO.getCity(),
                addressDTO.getState(),
                addressDTO.getZipCode(),
                addressDTO.getCountry()
        );
    }

    private Patient mapToUser(PatientDTO userDTO, Address address) {
        return new Patient(userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                userDTO.getPhone(),
                address,
                userDTO.getBirthday(),
                userDTO.getPatientInsuranceNumber());
    }

    private Doctor mapToDoctor(DoctorDTO doctorDTO) {
        return new Doctor(
                doctorDTO.getUsername(),
                doctorDTO.getPassword(),
                doctorDTO.getFirstName(),
                doctorDTO.getLastName(),
                doctorDTO.getEmail(),
                doctorDTO.getPhone(),
                doctorDTO.getOfficeName()
        );
    }

    private List<Medication> mapToMedicine(PrescriptionForDoctorDTO prescriptionDTO) {
        return new ArrayList<>(
                prescriptionDTO.getMedicine().stream()
                        .map(medicineDTO -> new Medication(
                                medicineDTO.getName(),
                                medicineDTO.getDescription(),
                                medicineDTO.getDosage(),
                                medicineDTO.getMedicationType())).toList());
    }

    private PatientResponseDTO mapToUserResponseDTO(Patient user, PrescriptionDTO prescriptionDTO) {

        AddressDTO addressDTO = prescriptionDTO.getPatient().getAddress();
        AddressResponseDTO addressResponseDTO = new AddressResponseDTO(
                addressDTO.getStreet(),
                addressDTO.getCity(),
                addressDTO.getState(),
                addressDTO.getZipCode(),
                addressDTO.getCountry()
        );
        return new PatientResponseDTO(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                addressResponseDTO,
                user.getBirthday(),
                user.getPatientInsuranceNumber()
        );
    }

    private PrescriptionResponseForPrescriptionDTO mapToResponseDTO(
            Prescription savedPrescription,
            PatientResponseDTO userResponseDTO,
            DoctorWithoutPrescriptionViewResponseDTO doctorResponseDTO,
            List<MedicationResponseDTO> medicineResponseDTOList) {
        return new PrescriptionResponseForPrescriptionDTO(
                userResponseDTO,
                doctorResponseDTO,
                savedPrescription.getPrescriptionDate(),
                savedPrescription.getPrescriptionType(),
                medicineResponseDTOList,
                savedPrescription.getQrCodes(),
                savedPrescription.getPharmacyCode(),
                savedPrescription.getPrescriptionStatus()
        );
    }

    private String getAllPrescriptionTypes() {
        return Arrays.stream(PrescriptionType.values())
                .filter(type -> !type.equals(PrescriptionType.NONE))
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    public String getPrescriptionsStatus() {
        return Arrays.stream(PrescriptionStatus.values())
                .filter(status -> !status.equals(PrescriptionStatus.NONEXISTENT))
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
