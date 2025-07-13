package com.otterdram.otterdram.common.audit.auditable;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * <pre>
 * createdAt + createdBy
 * updatedAt + updatedBy
 * </pre>
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Updatable extends Creatable {

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

}
