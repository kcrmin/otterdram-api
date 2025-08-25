package com.otterdram.otterdram.domain.spirits.company.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.otterdram.otterdram.common.enums.common.DataStatus;

import java.time.LocalDateTime;

public record CompanyResponse(
    Long id,

    @JsonUnwrapped
    CompanyBaseData companyBaseData,

    DataStatus status

//    String createdAt,
//    Long createdBy,
//    String updatedAt,
//    Long updatedBy
) {
}
