package com.otterdram.otterdram.domain.spirits.relation;

import com.otterdram.otterdram.common.audit.superclass.Creatable;
import com.otterdram.otterdram.domain.spirits.brand.Brand;
import com.otterdram.otterdram.domain.spirits.distillery.Distillery;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Join Table for the many-to-many relationship between brands and distilleries
 * <pre>
 * Table brand_distillery_relations {
 *   id bigint [pk, increment]
 *   brand_id bigint [ref: > brands.id, not null]
 *   distillery_id bigint [ref: > distilleries.id, not null]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 * }
 * </pre>
 */

@Entity
@Table(name = "brand_distillery_relations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandDistilleryRelation extends Creatable {

    @Id
    @SequenceGenerator(name = "brand_distillery_relation_seq", sequenceName = "brand_distillery_relation_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_distillery_relation_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distillery_id", nullable = false)
    private Distillery distillery;
}
