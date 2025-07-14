package com.otterdram.otterdram.common.audit.service;

import com.otterdram.otterdram.common.audit.superclass.Reviewable;
import com.otterdram.otterdram.common.enums.DataStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for entities that can be reviewed.
 *
 * @param <T> the type of the entity that extends Reviewable
 * @param <ID> the type of the entity's identifier (usually Long or String)
 */
public abstract class ReviewableService<T extends Reviewable, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    protected abstract Long getCurrentUserId();

    @Transactional
    public final void review(ID id, DataStatus status) {
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));

        entity.review(getCurrentUserId(), status);
        getRepository().save(entity);
    }
}
