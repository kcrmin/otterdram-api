package com.otterdram.otterdram.domain.spirits.company.dto;

import com.otterdram.otterdram.common.enums.common.LanguageCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record CompanyCreateRequest(
    Long parentCompanyId,

    String companyLogo,

    @NotBlank(message = "Company name must not be blank")
    @Size(max = 100, message = "Company name must be at most 100 characters long")
    String companyName,

    Map<LanguageCode, String> translations,

    Map<LanguageCode, String> descriptions,

    Boolean independentBottler,

    String schemaVersion
) {
}