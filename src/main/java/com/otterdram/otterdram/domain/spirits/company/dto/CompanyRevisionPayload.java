package com.otterdram.otterdram.domain.spirits.company.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import jakarta.validation.Valid;

public record CompanyRevisionPayload(
    @Valid
    @JsonUnwrapped
    CompanyBaseData companyBaseData,

    DataStatus snapshotStatus
) {
}
