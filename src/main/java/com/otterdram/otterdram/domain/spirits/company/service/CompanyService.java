package com.otterdram.otterdram.domain.spirits.company.service;

import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyBaseData;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyCreateRequest;
import com.otterdram.otterdram.common.audit.service.SoftDeletableService;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyResponse;
import com.otterdram.otterdram.domain.spirits.company.mapper.CompanyMapper;
import com.otterdram.otterdram.domain.spirits.company.repository.CompanyRepository;
import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;
import com.otterdram.otterdram.domain.spirits.revision.dto.RevisionResponse;
import com.otterdram.otterdram.domain.spirits.revision.mapper.RevisionMapper;
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
        // 1. 컴퍼니 이름으로 조회 존재한다면 예외 발생
        validateCompanyNameNotExists(request.companyBaseData().companyName());

        // 2. 컴퍼니 생성 및 저장
        Company company = Company.builder()
                .companyName(request.companyBaseData().companyName())
                .status(hasAdditionalData(request) ? DataStatus.IN_REVIEW : DataStatus.DRAFT)
                .build();
        Company savedCompany = companyRepository.save(company);

        // 3. 컴퍼니 리비전 생성 및 저장 (추가 데이터가 있는 경우에만)
        if (hasAdditionalData(request)) {
            EntityRevision revision = createRevision(savedCompany.getId(), request);
            revisionRepository.save(revision);
        }

        // 4. 컴퍼니 응답 반환
        return CompanyMapper.INSTANCE.toResponse(savedCompany);
    }

    @Transactional
    public RevisionResponse createCompanyRevision(Long companyId, CompanyCreateRequest request) {
        // 1. 컴퍼니 이름으로 조회
        Company existingCompany = getCompanyById(companyId);

        // 3. 컴퍼니 리비전 생성 및 저장
        EntityRevision revision = createRevision(existingCompany.getId(), request);
        EntityRevision savedRevision = revisionRepository.save(revision);

        // 4. 컴퍼니 상태 업데이트 (DRAFT -> IN_REVIEW)
        if (existingCompany.getStatus() == DataStatus.DRAFT) {
            existingCompany.setStatus(DataStatus.IN_REVIEW);
            companyRepository.save(existingCompany);
        }

        // 5. 컴퍼니 응답 반환
        return RevisionMapper.INSTANCE.toResponse(savedRevision);
    }

    private void validateCompanyNameNotExists(String companyName) {
        companyRepository.findByCompanyName(companyName)
            .ifPresent(company -> {
                    throw new IllegalArgumentException("Company with name " + companyName + " already exists.");
            });
    }
    private Company getCompanyById(Long companyId) {
        return companyRepository.findByIdAndDeletedAtIsNull(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Company with ID " + companyId + " does not exist."));
    }
    private boolean hasAdditionalData(CompanyCreateRequest request) {
        return request.companyBaseData().parentCompanyId() != null ||
                request.companyBaseData().companyLogo() != null ||
                !request.companyBaseData().translations().isEmpty() ||
                !request.companyBaseData().descriptions().isEmpty() ||
                request.companyBaseData().independentBottler() != null;
    }
    private EntityRevision createRevision(Long companyId, CompanyCreateRequest request) {
        return EntityRevision.builder()
                .entityType(RevisionTargetEntity.COMPANY)
                .entityId(companyId)
                .schemaVersion(request.schemaVersion())
                .revisionData(new CompanyBaseData(
                        request.companyBaseData().parentCompanyId(),
                        request.companyBaseData().companyLogo(),
                        request.companyBaseData().companyName(),
                        request.companyBaseData().translations(),
                        request.companyBaseData().descriptions(),
                        request.companyBaseData().independentBottler()
                ))
                .diffData(null)
                .build();
    }
}
