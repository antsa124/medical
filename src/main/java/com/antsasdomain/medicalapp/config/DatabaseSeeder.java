package com.antsasdomain.medicalapp.config;

import com.antsasdomain.medicalapp.model.*;
import com.antsasdomain.medicalapp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final DoctorRepository doctorRepository;
    private final MedicationRepository medicationRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final PharmacistRepository pharmacistRepository;

    public DatabaseSeeder(
            DoctorRepository doctorRepository,
            MedicationRepository medicationRepository,
            PrescriptionRepository prescriptionRepository,
            PatientRepository patientRepository,
            PharmacistRepository pharmacistRepository) {
        this.doctorRepository = doctorRepository;
        this.medicationRepository = medicationRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.pharmacistRepository = pharmacistRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedDatabase();
    }

    private void seedDatabase() {
        if (doctorRepository.count() == 0 && patientRepository.count() == 0) {
            Doctor doctor1 = new Doctor(
                    "john2doe",
                    "Seewruoo0!",
                    "John",
                    "Doe",
                    "john@doe.com",
                    "01234566123",
                    "John Doe's office" );
            Doctor doctor2 = new Doctor(
                    "jane2dae",
                    "qWejioeu891!!",
                    "Jane",
                    "Dae",
                    "jane@dae.com",
                    "01234506123",
                    "Jane Dae's office" );
            Doctor doctor3 = new Doctor(
                    "mydoctorlib",
                    "qWejioeu80ew1!!",
                    "Suzan",
                    "Cooper",
                    "cooper@dae.com",
                    "01234506123",
                    "At cooper's" );
            Doctor doctor4 = new Doctor(
                    "johanna",
                    "qWejioeu891!!",
                    "Johanna",
                    "Sting",
                    "johanna@example.com",
                    "01234506123",
                    "Johanna's office" );

            doctorRepository.saveAll(List.of(doctor1, doctor2, doctor3, doctor4));

            Medication medication1 = new Medication("Aspirin", "Pain relief", "1 pill per day",
                    MedicationType.PILL);
            Medication medication2 = new Medication("Paracetamol", "Reduce headache and fever", "2 " +
                    "pills every 6 hours", MedicationType.PILL);
            Medication medication3 = new Medication("Voltaren", "Relieve muscle and joint " +
                    "stiffness pain", "1 pea-sized per affected area", MedicationType.CREAM);
            Medication medication4 = new Medication("Quetiapine", "treat mental conditions " +
                    "including bipolar, schizophrenia",
                    "50 mg per day", MedicationType.PILL);

            medicationRepository.saveAll(List.of(medication1, medication2, medication3, medication4));

            Address address1 = new Address("Baker Street 21", "Hamburg", "Hamburg", "21073", "Germany");
            Address address2 = new Address("Brahmsstraße 32b", "Hamburg", "Hamburg", "20251",
                    "Germany");
            Address address3 = new Address("Bremer Straße 172", "Hamburg", "Hamburg", "21073",
                    "Germany");
            Address address4 = new Address("Hohelufbrücke 17", "Hamburg", "Hamburg", "20251",
                    "Germany");

            Patient patient1 = new Patient("patient1", "Securepass3!", "David", "Miller", "david" +
                    "@example.com"
                    , "11122233309", address1, LocalDate.of(1985, 4, 12),
                    "G23489459999");

            Patient patient2 = new Patient("patient2", "Sec0repass4!", "Dan", "Mueller", "dan" +
                    "@example.com"
                    , "11122233309", address2, LocalDate.of(1986, 10, 13),
                    "G890983249");

            Patient patient3 = new Patient("patient3", "Securepass3!", "Danielle", "Weiß",
                    "danielle" +
                    "@example.com"
                    , "11122233309", address3, LocalDate.of(1985, 4, 12),
                    "G099866090");

            Patient patient4 = new Patient("patient4", "Sec0repass4!", "Dani", "Meyer", "dani" +
                    ".meyer@example.com"
                    , "11122233309", address4, LocalDate.of(1986, 10, 13),
                    "G912003089");

            patientRepository.saveAll(List.of(patient1, patient2, patient3, patient4));

            // Insert Prescriptions (Linked to Doctors, Patients, and Medications)
            Prescription prescription1 = new Prescription(
                    patient1,
                    doctor1,
                    LocalDate.of(2025, 2, 9),
                    PrescriptionType.PRIVAT,
                    List.of(medication1),
                    List.of("qrcode1", "qrcode2"),
                    "PHARMA0001",
                    PrescriptionStatus.ACTIVE);

            Prescription prescription2 = new Prescription(
                    patient2,
                    doctor2,
                    LocalDate.of(2025, 2, 9),
                    PrescriptionType.E_REZEPT,
                    List.of(medication2),
                    List.of("qrcode3", "qrcode4"),
                    "PHARMA0002",
                    PrescriptionStatus.ACTIVE);

            Prescription prescription3 = new Prescription(
                    patient3,
                    doctor3,
                    LocalDate.of(2025, 2, 9),
                    PrescriptionType.PRIVAT,
                    List.of(medication1),
                    List.of("qrcode1", "qrcode2"),
                    "PHARMA0001",
                    PrescriptionStatus.ACTIVE);

            Prescription prescription4 = new Prescription(
                    patient4,
                    doctor4,
                    LocalDate.of(2025, 2, 9),
                    PrescriptionType.E_REZEPT,
                    List.of(medication2),
                    List.of("qrcode3", "qrcode4"),
                    "PHARMA0002",
                    PrescriptionStatus.ACTIVE);

            prescriptionRepository.saveAll(List.of(prescription1, prescription2, prescription3, prescription4));

            Pharmacist pharmacist1 = new Pharmacist(
                    "pharmaFirst",
                    "werjlEj!890",
                    "Lorelay",
                    "Lois",
                    "example.mail@mail.com",
                    "12345678900",
                    "Nord Apotheke",
                    "PHARMA0001"
            );

            Pharmacist pharmacist2 = new Pharmacist(
                    "pharmaSecond",
                    "werjlEj!890",
                    "Lorelay",
                    "Lois",
                    "example.mail2@mail.com",
                    "12345678900",
                    "Farmacia Hamburg",
                    "PHARMA0002"
            );

            pharmacistRepository.saveAll(List.of(pharmacist1, pharmacist2));
        }
    }
}
