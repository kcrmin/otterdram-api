package com.otterdram.otterdram.domain.spirits.revision.repository;

import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;
import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RevisionRepository extends JpaRepository<EntityRevision, Long> {
    Optional<EntityRevision> findByEntityTypeAndEntityIdAndStatus(RevisionTargetEntity entityType, Long entityId, RevisionStatus status);
}
