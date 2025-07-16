package com.otterdram.otterdram.domain.spirits.tag.repository;

import com.otterdram.otterdram.common.enums.target.TagTargetEntity;
import com.otterdram.otterdram.domain.spirits.tag.EntityTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<EntityTag, Long> {

    List<EntityTag> findByEntityTypeAndEntityId(TagTargetEntity entityType, Long entityId);

    List<EntityTag> findByEntityType(TagTargetEntity entityType);

    List<EntityTag> findByTagName(String tagName);
}
