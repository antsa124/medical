INSERT INTO doctor (first_name, last_name, email, phone, office_name)
VALUES ('John', 'Doe', 'john.doe@example.com', '123456789', 'Doe Office'),
       ('Alice', 'Smith', 'alice.smith@example.com', '987654321', 'Ms. Smith Office');

INSERT INTO medication (name, description, dosage)
VALUES ('Aspirin', 'Pain relief', '1 pill per day'),
       ('Paracetamol', 'Fever reducer', '2 pills every 6 hours');

INSERT INTO prescription (doctor_id, prescription_date, prescription_type, pharmacy_code)
VALUES (1, '2025-02-09', 'E-REZEPT', 'PHARM001'),
       (2, '2025-02-10', 'PRIVAT', 'PHARM002');

INSERT INTO users (username, password, first_name, last_name, email, phone, birthday)
VALUES ('patient1', 'securepass', 'David', 'Miller', 'david@example.com', '111222333',
        '1985-04-12'),
       ('patient2', 'securepass', 'Emily', 'Brown', 'emily@example.com', '444555666', '1992-07-19');

INSERT INTO address (street, city, state, zip_code, country)
VALUES ('123 Main St', 'Berlin', 'BE', '10115', 'Germany'),
       ('456 Park Ave', 'Munich', 'BY', '80331', 'Germany');