package com.otterdram.otterdram.domain.spirits.company.dto;

import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;

import java.util.Map;

public record CompanyResponse(
    Long id,
    Long parentCompanyId,
    String companyLogo,
    String companyName,
    Map<LanguageCode, String> translations,
    Map<LanguageCode, String> descriptions,
    Boolean independentBottler,
    DataStatus status
) {
}
