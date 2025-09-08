package com.otterdram.otterdram.common.audit.service;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for entities that can be soft-deleted and restored.
 *
 * @param <T> the type of the entity that extends SoftDeletable
 * @param <ID> the type of the entity's identifier (usually Long or String)
 */

@Service
public abstract class SoftDeletableService<T extends SoftDeletable, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    protected Long getCurrentUserId() {
        // TODO: getCurrentUserId
        return 0L;
    };

    @Transactional(readOnly = true)
    public boolean isSoftDeleted(ID id) {
        return getRepository().findById(id)
                .map(SoftDeletable::isDeleted)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
    }

    @Transactional
    public void softDelete(ID id) {
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));

        entity.softDelete(getCurrentUserId());
        getRepository().save(entity);
    }

    @Transactional
    public void restore(ID id) {
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));

        entity.restore();
        getRepository().save(entity);
    }
}
