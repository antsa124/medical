package com.antsasdomain.medicalapp.validation;

import com.antsasdomain.medicalapp.model.MedicationType;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class MedicationTypeDeserializer extends JsonDeserializer<MedicationType> {

    @Override
    public MedicationType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText().trim();

        if (value.isEmpty()) {
            return MedicationType.NONE;
        }

        try {
            return MedicationType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return MedicationType.NONE;
        }

    }

}
