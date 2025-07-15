package com.otterdram.otterdram.common.audit.service;

import com.otterdram.otterdram.common.audit.superclass.AuthorModifiable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for entities that can be modified by an author.
 *
 * @param <T> the type of the entity that extends AuthorModifiable
 * @param <ID> the type of the entity's identifier (usually Long or String)
 */

@Service
public abstract class AuthorModifiableService<T extends AuthorModifiable, ID> extends SoftDeletableService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    protected abstract Long getCurrentUserId();

    @Transactional
    public final void modify(ID id) {
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));

        entity.markModified();
        getRepository().save(entity);
    }
}
