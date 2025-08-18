package com.otterdram.otterdram.common.audit.superclass;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * <pre>
 * createdAt + createdBy
 * modifiedAt
 * updatedAt + updatedBy
 * deletedAt + deletedBy
 * </pre>
 */

@Getter
@SuperBuilder
@MappedSuperclass
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AuthorModifiable extends SoftDeletable {

    @Column(name = "modified_at", columnDefinition = "timestamptz(6)")
    private Instant modifiedAt;

    public boolean isModified() {
        return modifiedAt != null;
    }

    public void markModified() {
        this.modifiedAt = Instant.now();
    }
}
