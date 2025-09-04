package com.otterdram.otterdram.domain.spirits.revision.service;

import com.otterdram.otterdram.common.audit.service.SoftDeletableService;
import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;
import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;
import com.otterdram.otterdram.domain.spirits.revision.RevisableEntity;
import com.otterdram.otterdram.domain.spirits.revision.dto.RevisionResponse;
import com.otterdram.otterdram.domain.spirits.revision.mapper.RevisionMapper;
import com.otterdram.otterdram.domain.spirits.revision.repository.RevisionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class RevisableEntityService<
    E extends SoftDeletable & RevisableEntity,
    ID,
    REQ,
    RESP,
    PAYLOAD> extends SoftDeletableService<E, ID> {

    @Override
    protected abstract JpaRepository<E, ID> getRepository();
    protected abstract RevisionRepository getRevisionRepository();
    protected abstract RevisionTargetEntity targetEntity();
    protected abstract void checkUniqueness(REQ req);
    protected abstract boolean hasAdditionalData(REQ req);
    protected abstract E toEntity(REQ req, DataStatus status);
    protected abstract RESP toResponse(E entity);
    protected abstract PAYLOAD toRevisionPayload(E entity, REQ req);


    @Transactional
    public RESP create(REQ request) {
        checkUniqueness(request);

        DataStatus status = hasAdditionalData(request) ? DataStatus.IN_REVIEW : DataStatus.DRAFT;
        E entity = toEntity(request, status);
        E saved = getRepository().save(entity);

        if (hasAdditionalData(request)) {
            createAndSaveRevision(saved, request);
        }
        return toResponse(saved);
    }

    @Transactional
    public RevisionResponse createRevision(ID id, REQ request) {
        E existing = getRepository().findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Entity with id '" + id + "' not found."));

        getRevisionRepository()
                .findByEntityTypeAndEntityIdAndStatus(targetEntity(), existing.getId(), RevisionStatus.IN_REVIEW)
                .ifPresent(revision -> { throw new IllegalArgumentException("There is already a pending revision for this entity."); });

        if (existing.getStatus() == DataStatus.SUPPRESSED) {
            throw new IllegalStateException("Cannot create revision for a suppressed entity.");
        }
        if (existing.getStatus() == DataStatus.IN_REVIEW) {
            throw new IllegalStateException("Cannot create revision for an entity that is already in review.");
        }

        existing.updateStatus(DataStatus.IN_REVIEW);
        getRepository().save(existing);

        EntityRevision savedRevision = createAndSaveRevision(existing, request);
        return RevisionMapper.INSTANCE.toResponse(savedRevision);
    }

    private EntityRevision createAndSaveRevision(E entity, REQ request) {
        PAYLOAD payload = toRevisionPayload(entity, request);
        EntityRevision revision = EntityRevision.builder()
            .entityType(targetEntity())
            .entityId(entity.getId())
            .status(RevisionStatus.IN_REVIEW)
            .revisionData(payload)
            .diffData(null)
            .build();
        return getRevisionRepository().save(revision);
    }
}
