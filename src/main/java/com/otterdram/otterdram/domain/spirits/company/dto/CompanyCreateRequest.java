package com.otterdram.otterdram.domain.spirits.company.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record CompanyCreateRequest(
    String schemaVersion,

    @Valid
    CompanyBaseData companyBaseData
) {
}