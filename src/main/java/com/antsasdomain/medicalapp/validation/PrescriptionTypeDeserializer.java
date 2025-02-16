package com.antsasdomain.medicalapp.validation;

import com.antsasdomain.medicalapp.model.PrescriptionStatus;
import com.antsasdomain.medicalapp.model.PrescriptionType;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class PrescriptionTypeDeserializer extends JsonDeserializer<PrescriptionType> {
    @Override
    public PrescriptionType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText().trim();

        if (value.isEmpty()) {
            return PrescriptionType.NONE;
        }

        try {
            return PrescriptionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PrescriptionType.NONE;
        }
    }
}
