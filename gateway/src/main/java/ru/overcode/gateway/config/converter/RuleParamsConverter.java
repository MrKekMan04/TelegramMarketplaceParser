package ru.overcode.gateway.config.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class RuleParamsConverter implements AttributeConverter<Map<String, String>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> convertToEntityAttribute(String dbData) {
        try {
            return MAPPER.readValue(dbData, Map.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
