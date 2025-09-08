package com.otterdram.otterdram.domain.spirits.revision.dto;

import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;

public record RevisionResponse(
    Long id,
    RevisionTargetEntity entityType,
    Long entityId,
    String schemaVersion,
    Object revisionData,
    Object diffData,
    RevisionStatus status
//    String createdAt,
//    Long createdBy,
//    String reviewedAt,
//    Long reviewedBy
) {
}
