package com.otterdram.otterdram.domain.spirits.relation;

import com.otterdram.otterdram.common.audit.superclass.Creatable;
import com.otterdram.otterdram.common.enums.spirits.FillNumber;
import com.otterdram.otterdram.domain.spirits.cask.Cask;
import com.otterdram.otterdram.domain.spirits.release.Release;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Join Table for the many-to-many relationship between releases and casks
 * <pre>
 * Table release_cask_relations {
 *   id bigint [pk, increment]
 *   release_id bigint [ref: > releases.id, not null]
 *   fill_number FillNumber [not null, default: 'UNKNOWN']
 *   cask_id bigint [ref: > casks.id, not null]
 *   proportion decimal(5,2) [note: "비율(%)"]
 *   maturation_months smallint
 *   created_at timestamp [not null]
 *   created_by bigint [ref: > users.id, not null]
 * }
 * </pre>
 */

@Entity
@Table(name = "release_cask_relations",  uniqueConstraints = @UniqueConstraint(columnNames = {"release_id", "cask_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReleaseCaskRelation extends Creatable {

    @Id
    @SequenceGenerator(name = "release_cask_relation_seq", sequenceName = "release_cask_relation_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "release_cask_relation_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    @Enumerated(EnumType.STRING)
    @Column(name = "fill_number", nullable = false, columnDefinition = "varchar(20) default 'UNKNOWN'")
    private FillNumber fillNumbger = FillNumber.UNKNOWN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cask_id", nullable = false)
    private Cask cask;

    @Column(name = "proportion", precision = 5, scale = 2)
    private Double proportion;

    @Column(name = "maturation_months")
    private Short maturationMonths;
}
