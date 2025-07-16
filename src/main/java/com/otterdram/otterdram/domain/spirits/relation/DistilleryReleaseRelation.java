package com.otterdram.otterdram.domain.spirits.relation;

import com.otterdram.otterdram.common.audit.superclass.Creatable;
import com.otterdram.otterdram.domain.spirits.distillery.Distillery;
import com.otterdram.otterdram.domain.spirits.release.Release;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Join Table for the many-to-many relationship between releases and distilleries
 * <pre>
 * Table release_distillery_relations {
 *   id bigint [pk, increment]
 *   release_id bigint [ref: > releases.id, not null]
 *   distillery_id bigint [ref: > distilleries.id, not null]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 * }
 * </pre>
 */

@Entity
@Table(name = "distillery_release_relations",  uniqueConstraints = @UniqueConstraint(columnNames = {"distillery_id", "release_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DistilleryReleaseRelation extends Creatable {

    @Id
    @SequenceGenerator(name = "distillery_release_relation_seq", sequenceName = "distillery_release_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "distillery_release_relation_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distillery_id", nullable = false)
    private Distillery distillery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;
}
