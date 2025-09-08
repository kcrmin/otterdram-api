package com.otterdram.otterdram.domain.spirits.company.dto;

import jakarta.validation.Valid;

public record CompanyCreateRequest(
    String schemaVersion,

    @Valid
    CompanyBaseData companyBaseData
) {
}