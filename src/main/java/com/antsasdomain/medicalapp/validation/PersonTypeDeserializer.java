package com.antsasdomain.medicalapp.validation;

import com.antsasdomain.medicalapp.model.Person;
import com.antsasdomain.medicalapp.model.PersonType;
import com.antsasdomain.medicalapp.model.PrescriptionStatus;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class PersonTypeDeserializer extends JsonDeserializer<PersonType> {
    @Override
    public PersonType deserialize(JsonParser jsonParser,
                             DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText().trim();

        if (value.isEmpty()) {
            return PersonType.NONE;
        }

        try {
            return PersonType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PersonType.NONE;
        }
    }
}

