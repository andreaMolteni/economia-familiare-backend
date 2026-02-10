package com.andreamolteni.economia_familiare.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Converter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RecurringConverters {

    @Converter
    public static class MonthsJsonConverter extends JsonConverters.BaseListJsonConverter<Integer> {
        public MonthsJsonConverter() { super(new TypeReference<List<Integer>>() {}); }
    }

    @Converter
    public static class AmountsJsonConverter extends JsonConverters.BaseListJsonConverter<BigDecimal> {
        public AmountsJsonConverter() { super(new TypeReference<List<BigDecimal>>() {}); }
    }

    @Converter
    public static class DatesJsonConverter extends JsonConverters.BaseListJsonConverter<LocalDate> {
        public DatesJsonConverter() { super(new TypeReference<List<LocalDate>>() {}); }
    }
}
