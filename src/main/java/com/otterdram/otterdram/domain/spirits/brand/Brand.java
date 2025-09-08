package com.otterdram.otterdram.domain.spirits.brand;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.domain.spirits.collection.Collection;


import com.otterdram.otterdram.domain.spirits.revision.RevisableEntity;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.relation.DistilleryBrandRelation;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Brand Entity
 * <pre>
 * Table brands {
 *   id bigint [pk, increment]
 *   company_id bigint [ref: > companies.id]
 *   brand_logo varchar(255)
 *   brand_name varchar(100) [not null, unique]
 *   translations jsonb [note: "다국어 지원 이름"]
 *   descriptions jsonb [note: "다국어 지원"]
 *   status DataStatus [not null, default: 'IN_REVIEW']
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   updated_by bigint [ref: > users.id, not null]
 *   deleted_at timestamp
 *   deleted_by bigint [ref: > users.id]
 * }
 * </pre>
 */

@Entity
@Table(name = "brands")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends SoftDeletable /*implements RevisableEntity*/ {

    @Id
    @SequenceGenerator(name = "brand_seq", sequenceName = "brand_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "brand_logo", length = 255)
    private String brandLogo;

    @Column(name = "brand_name", nullable = false, length = 100, unique = true)
    private String brandName;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "jsonb")
    private Map<LanguageCode, String> translations;

    @Type(JsonType.class)
    @Column(name = "descriptions", columnDefinition = "jsonb")
    private Map<LanguageCode, String> descriptions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'IN_REVIEW'")
    private DataStatus status = DataStatus.IN_REVIEW;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<DistilleryBrandRelation> distilleryBrandRelations = new ArrayList<>();

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<Collection> collections = new ArrayList<>();
}
