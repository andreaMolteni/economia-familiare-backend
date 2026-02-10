package com.andreamolteni.economia_familiare.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;

import java.util.List;

public class JsonConverters {

    //static final ObjectMapper MAPPER = new ObjectMapper();

    static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());




    public static abstract class BaseListJsonConverter<T> implements AttributeConverter<List<T>, String> {
        private final TypeReference<List<T>> typeRef;

        protected BaseListJsonConverter(TypeReference<List<T>> typeRef) {
            this.typeRef = typeRef;
        }

        @Override
        public String convertToDatabaseColumn(List<T> attribute) {
            try {
                if (attribute == null) return "[]";
                return MAPPER.writeValueAsString(attribute);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot serialize list to JSON", e);
            }
        }

        @Override
        public List<T> convertToEntityAttribute(String dbData) {
            try {
                if (dbData == null || dbData.isBlank()) return List.of();
                return MAPPER.readValue(dbData, typeRef);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot deserialize JSON to list", e);
            }
        }
    }
}
