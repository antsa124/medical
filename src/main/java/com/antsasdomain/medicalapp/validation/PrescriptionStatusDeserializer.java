package com.antsasdomain.medicalapp.validation;

import com.antsasdomain.medicalapp.model.MedicationType;
import com.antsasdomain.medicalapp.model.PrescriptionStatus;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class PrescriptionStatusDeserializer extends JsonDeserializer<PrescriptionStatus> {
    @Override
    public PrescriptionStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText().trim();

        if (value.isEmpty()) {
            return PrescriptionStatus.NONEXISTENT;
        }

        try {
            return PrescriptionStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PrescriptionStatus.NONEXISTENT;
        }
    }
}
