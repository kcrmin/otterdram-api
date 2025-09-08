package com.otterdram.otterdram.domain.spirits.company.service;

import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyCreateRequest;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyResponse;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyRevisionPayload;
import com.otterdram.otterdram.domain.spirits.company.mapper.CompanyMapper;
import com.otterdram.otterdram.domain.spirits.company.repository.CompanyRepository;
import com.otterdram.otterdram.domain.spirits.revision.service.RevisableEntityService;
import com.otterdram.otterdram.domain.spirits.revision.repository.RevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService extends RevisableEntityService<Company, Long, CompanyCreateRequest, CompanyResponse, CompanyRevisionPayload> {
    private final CompanyRepository companyRepository;
    private final RevisionRepository revisionRepository;

    @Override protected JpaRepository<Company, Long> getRepository() {
        return companyRepository;
    }

    @Override protected RevisionRepository getRevisionRepository() {
        return revisionRepository;
    }

    @Override protected RevisionTargetEntity targetEntity() {
        return RevisionTargetEntity.COMPANY;
    }

    @Override
    protected void checkUniqueness(CompanyCreateRequest companyCreateRequest) {
        String name = companyCreateRequest.companyBaseData().companyName();
        if (companyRepository.existsByCompanyName(name)) {
            throw new IllegalArgumentException("Company with name '" + name + "' already exists.");
        }
    }

    @Override
    protected boolean hasAdditionalData(CompanyCreateRequest companyCreateRequest) {
        return companyCreateRequest.companyBaseData().parentCompanyId() != null ||
                companyCreateRequest.companyBaseData().companyLogo() != null ||
                !companyCreateRequest.companyBaseData().translations().isEmpty() ||
                !companyCreateRequest.companyBaseData().descriptions().isEmpty() ||
                companyCreateRequest.companyBaseData().independentBottler() != null;
    }

    @Override
    protected Company toEntity(CompanyCreateRequest companyCreateRequest, DataStatus status) {
        return Company.builder()
                .companyName(companyCreateRequest.companyBaseData().companyName())
                .status(status)
                .build();
    }

    @Override
    protected CompanyResponse toResponse(Company entity) {
        return CompanyMapper.INSTANCE.toResponse(entity);
    }

    @Override
    protected CompanyRevisionPayload toRevisionPayload(Company entity, CompanyCreateRequest companyCreateRequest) {
        return new CompanyRevisionPayload(
                companyCreateRequest.companyBaseData(),
                entity.getStatus()
        );
    }
}
