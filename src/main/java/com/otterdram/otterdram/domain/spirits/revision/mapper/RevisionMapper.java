package com.otterdram.otterdram.domain.spirits.revision.mapper;

import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;
import com.otterdram.otterdram.domain.spirits.revision.dto.RevisionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RevisionMapper {
    RevisionMapper INSTANCE = Mappers.getMapper(RevisionMapper.class);

    RevisionResponse toResponse(EntityRevision revision);
}
