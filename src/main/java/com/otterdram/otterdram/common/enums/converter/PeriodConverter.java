package com.otterdram.otterdram.common.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Period;

@Converter(autoApply = true)
public class PeriodConverter implements AttributeConverter<Period, String> {

    @Override
    public String convertToDatabaseColumn(Period period) {
        return period == null ? null : period.toString();
    }

    @Override
    public Period convertToEntityAttribute(String dbData) {
        return dbData == null || dbData.isEmpty() ? null : Period.parse(dbData);
    }
}
