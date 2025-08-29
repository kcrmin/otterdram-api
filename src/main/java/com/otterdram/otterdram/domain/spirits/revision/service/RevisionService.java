package com.otterdram.otterdram.domain.spirits.revision.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otterdram.otterdram.common.audit.service.ReviewableService;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyRevisionPayload;
import com.otterdram.otterdram.domain.spirits.company.repository.CompanyRepository;
import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;
import com.otterdram.otterdram.domain.spirits.revision.repository.RevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RevisionService extends ReviewableService<EntityRevision, Long> {
    private final RevisionRepository revisionRepository;
    private final CompanyRepository companyRepository;

    @Override
    protected JpaRepository<EntityRevision, Long> getRepository() {
        return revisionRepository;
    }

    @Transactional
    public void approve(Long revisionId) {
        processRevision(revisionId, RevisionStatus.APPROVED);
    }

    @Transactional
    public void reject(Long revisionId) {
        processRevision(revisionId, RevisionStatus.REJECTED);
    }

    private void processRevision(Long revisionId, RevisionStatus status) {
        // 1. 리비전 조회
        EntityRevision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new IllegalArgumentException("Revision not found"));

        // 2. 엔티티 타입별 처리
        processEntityByStatus(revision, status);

        // 3. 리비전 상태 업데이트
        this.review(revision.getId(), status);
    }

    private void processEntityByStatus(EntityRevision revision, RevisionStatus status) {
        switch (revision.getEntityType()) {
            case COMPANY -> processCompanyRevision(revision, status);
            // 다른 엔티티 타입에 대한 처리 로직 추가 가능
            default -> throw new UnsupportedOperationException("Unsupported entity type for processing");
        }
    }

    private void processCompanyRevision(EntityRevision revision, RevisionStatus status) {
        // 1. 컴퍼니 조회
        Company company = companyRepository.findById(revision.getEntityId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found for revision entityId " + revision.getEntityId()));

        // 2. 컴퍼니 상태 확인
        if (company.getStatus() != DataStatus.IN_REVIEW) {
            throw new IllegalStateException("Company is not in review status");
        }

        // 3. 리비전 데이터로 컴퍼니 정보 업데이트
        ObjectMapper objectMapper = new ObjectMapper();
        CompanyRevisionPayload revisionData = objectMapper.convertValue(revision.getRevisionData(), CompanyRevisionPayload.class);

        if (status == RevisionStatus.APPROVED) {
            Company updatedCompany = company.toBuilder()
                    .parentCompany(revisionData.companyBaseData().parentCompanyId() != null ? companyRepository.getReferenceById(revisionData.companyBaseData().parentCompanyId()) : null)
                    .companyLogo(revisionData.companyBaseData().companyLogo())
                    .companyName(revisionData.companyBaseData().companyName())
                    .translations(revisionData.companyBaseData().translations())
                    .descriptions(revisionData.companyBaseData().descriptions())
                    .independentBottler(revisionData.companyBaseData().independentBottler())
                    .status(DataStatus.CONFIRMED)
                    .build();
            companyRepository.save(updatedCompany);
        } else if (status == RevisionStatus.REJECTED) {
            Company updatedCompany = company.toBuilder()
                    .status(revisionData.snapshotStatus())
                    .build();
            companyRepository.save(updatedCompany);
        }
    }
}
