package com.otterdram.otterdram.domain.spirits.revision.dto;

import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;

import java.util.Map;

public record RevisionResponse(
    Long id,
    RevisionTargetEntity entityType,
    Long entityId,
    String schemaVersion,
    Map<String, Object> revisionData,
    Map<String, Object> diffData,
    RevisionStatus status
){
}