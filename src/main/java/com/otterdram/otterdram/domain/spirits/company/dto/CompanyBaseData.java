package com.otterdram.otterdram.domain.spirits.company.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record CompanyBaseData(
    Long parentCompanyId,
    String companyLogo,

    @NotBlank(message = "Company name must not be blank")
    @Size(max = 100, message = "Company name must be at most 100 characters long")
    String companyName,

    @JsonProperty("translations")
    Map<LanguageCode, String> translations,

    @JsonProperty("descriptions")
    Map<LanguageCode, String> descriptions,

    Boolean independentBottler
) {
    public CompanyBaseData {
        translations = translations != null ? translations : Map.of();
        descriptions = descriptions != null ? descriptions : Map.of();
    }
}
