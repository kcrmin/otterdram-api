package com.otterdram.otterdram.domain.spirits.revision.service;

import com.otterdram.otterdram.common.audit.service.ReviewableService;
import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;
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

    @Override
    protected Long getCurrentUserId() {
        // TODO: getCurrentUserId
        return 0L;
    }

    @Transactional
    public void approveCompanyRevision(Long revisionId) {
        // 1. Get the revision by ID
        EntityRevision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new IllegalArgumentException("Revision not found"));

        // 2. Check if the revision is for a company
        if (revision.getEntityType() != RevisionTargetEntity.COMPANY) {
            throw new IllegalArgumentException("Revision is not for a company");
        }

        // 3. Get the company entity from the revision
        var company = companyRepository.getReferenceById(revision.getEntityId());

        // 4. Apply the changes using the mapper
        companyRepository.save(company);

        // 5. Mark the revision as approved
        review(revision.getId(), RevisionStatus.APPROVED);
    }
}
