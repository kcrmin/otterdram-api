package com.otterdram.otterdram.domain.spirits.distillery;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.spirits.DistilleryOperationalStatus;
import com.otterdram.otterdram.common.geo.address.Address;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.relation.DistilleryBrandRelation;
import com.otterdram.otterdram.domain.spirits.relation.DistilleryReleaseRelation;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Distillery Entity
 * <pre>
 * Table distilleries {
 *   id bigint [pk, increment]
 *   company_id bigint [ref: > companies.id]
 *   distillery_logo varchar(255)
 *   distillery_name varchar(100) [not null, unique]
 *   translations jsonb [note: "다국어 지원 이름"]
 *   descriptions jsonb [note: "다국어 지원"]
 *   country_id bigint [ref: > countries.id]
 *   city_id bigint [ref: > cities.id]
 *   address varchar(255)
 *   operational_status DistilleryOperationalStatus [not null, default: 'UNKNOWN']
 *   status DataStatus [not null, default: 'DRAFT']
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
@Table(name = "distilleries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Distillery extends SoftDeletable {

    @Id
    @SequenceGenerator(name = "distillery_seq", sequenceName = "distillery_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "distillery_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "distillery_logo", length = 255)
    private String distilleryLogo;

    @Column(name = "distillery_name", nullable = false, length = 100, unique = true)
    private String distilleryName;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "jsonb")
    private Map<LanguageCode, String> translations;

    @Type(JsonType.class)
    @Column(name = "descriptions", columnDefinition = "jsonb")
    private Map<LanguageCode, String> descriptions;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "operational_status", nullable = false, columnDefinition = "varchar(20) default 'UNKNOWN'")
    private DistilleryOperationalStatus operationalStatus = DistilleryOperationalStatus.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'DRAFT'")
    private DataStatus status = DataStatus.DRAFT;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "distillery", fetch = FetchType.LAZY)
    private List<DistilleryBrandRelation> distilleryBrandRelations = new ArrayList<>();

    @OneToMany(mappedBy = "distillery", fetch = FetchType.LAZY)
    private List<DistilleryReleaseRelation> distilleryReleaseRelations = new ArrayList<>();
}
