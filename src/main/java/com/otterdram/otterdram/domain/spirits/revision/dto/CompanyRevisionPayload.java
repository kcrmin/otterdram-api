package com.otterdram.otterdram.domain.spirits.revision.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otterdram.otterdram.common.enums.common.LanguageCode;

import java.util.Map;

public record CompanyRevisionPayload(
    Long parentCompanyId,
    String companyLogo,
    String companyName,
    Map<LanguageCode, String> translations,
    Map<LanguageCode, String> descriptions,
    Boolean independentBottler
) {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Map<String, Object> toMap() {
        return OBJECT_MAPPER.convertValue(this, new TypeReference<Map<String, Object>>() {});
    }
}
