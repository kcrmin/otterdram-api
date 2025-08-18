package com.otterdram.otterdram.domain.spirits.company.mapper;

import com.otterdram.otterdram.domain.spirits.company.dto.CompanyResponse;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.revision.dto.CompanyRevisionPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    @Mapping(target = "parentCompanyId", source = "parentCompany.id")
    CompanyResponse toResponse(Company company);
}
