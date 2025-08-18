package com.otterdram.otterdram.domain.spirits.company.service;

import com.otterdram.otterdram.domain.spirits.company.dto.CompanyCreateRequest;
import com.otterdram.otterdram.common.audit.service.SoftDeletableService;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyResponse;
import com.otterdram.otterdram.domain.spirits.company.mapper.CompanyMapper;
import com.otterdram.otterdram.domain.spirits.company.repository.CompanyRepository;
import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;
import com.otterdram.otterdram.domain.spirits.revision.dto.CompanyRevisionPayload;
import com.otterdram.otterdram.domain.spirits.revision.repository.RevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService extends SoftDeletableService<Company, Long> {
    private final CompanyRepository companyRepository;
    private final RevisionRepository revisionRepository;

    @Override
    protected JpaRepository<Company, Long> getRepository() {
        return companyRepository;
    }

    @Override
    protected Long getCurrentUserId() {
        // TODO: getCurrentUserId
        return 0L;
    }

    @Transactional
    public CompanyResponse createCompany(CompanyCreateRequest request) {
        // 1. 컴퍼니 존재하는지 확인
        var existingCompany = companyRepository.findByCompanyName(request.companyName());

        // 2. 존재한다면 예외 발생
        if (existingCompany.isPresent()) {
            throw new IllegalArgumentException("Company with name " + request.companyName() + " already exists.");
        }

        // 3. 컴퍼니 생성 및 저장
        Company company = Company.builder()
                .companyName(request.companyName())
                .build();
        Company savedCompany = companyRepository.save(company);

        // 4. 컴퍼니 리비전 생성 및 저장
        EntityRevision revision = EntityRevision.builder()
                .entityType(RevisionTargetEntity.COMPANY)
                .entityId(savedCompany.getId())
                .schemaVersion(request.schemaVersion())
                .revisionData(new CompanyRevisionPayload(
                        request.parentCompanyId(),
                        request.companyLogo(),
                        request.companyName(),
                        request.translations(),
                        request.descriptions(),
                        request.independentBottler()
                        ).toMap())
                .diffData(null)
                .build();
        revisionRepository.save(revision);

        // 5. 컴퍼니 응답 반환
        return CompanyMapper.INSTANCE.toResponse(savedCompany);
    }
}
