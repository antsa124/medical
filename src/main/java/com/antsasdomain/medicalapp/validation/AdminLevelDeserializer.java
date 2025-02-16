package com.antsasdomain.medicalapp.validation;

import com.antsasdomain.medicalapp.model.AdminLevel;
import com.antsasdomain.medicalapp.model.PersonType;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class AdminLevelDeserializer extends JsonDeserializer<AdminLevel> {
    @Override
    public AdminLevel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText().trim();

        if (value.isEmpty()) {
            return AdminLevel.NONE;
        }
        try {
            return AdminLevel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AdminLevel.NONE;
        }
    }
}
