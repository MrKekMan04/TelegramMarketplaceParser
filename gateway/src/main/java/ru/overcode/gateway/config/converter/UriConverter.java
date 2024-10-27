package ru.overcode.gateway.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.net.URI;

@Converter
public class UriConverter implements AttributeConverter<URI, String> {

    @Override
    public String convertToDatabaseColumn(URI attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public URI convertToEntityAttribute(String dbData) {
        return URI.create(dbData);
    }
}
