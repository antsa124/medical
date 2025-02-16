package com.antsasdomain.medicalapp.model;

public enum PrescriptionStatus {
    ACTIVE,      // The prescription is still valid and can be used
    CANCELED,    // The doctor canceled it
    EXPIRED,     // The prescription is no longer valid after a certain period
    FULFILLED, // The prescription has already been used by a pharmacy
    NONEXISTENT
}
