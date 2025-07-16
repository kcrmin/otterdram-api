package com.otterdram.otterdram.domain.spirits.brand;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.DataStatus;
import com.otterdram.otterdram.common.enums.LanguageCode;
import com.otterdram.otterdram.domain.spirits.collection.Collection;
import com.otterdram.otterdram.common.geo.address.Address;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.domain.spirits.collection.Collection;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.distillery.Distillery;
import com.otterdram.otterdram.domain.spirits.relation.BrandDistilleryRelation;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
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
 *   translations text [note: "다국어 지원 이름"]
 *   descriptions text [note: "다국어 지원"]
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
@Table(name = "brands")
public class Brand extends SoftDeletable {

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
    @Column(name = "translations", columnDefinition = "json")
    private Map<LanguageCode, String> translations;

    @Type(JsonType.class)
    @Column(name = "descriptions", columnDefinition = "json")
    private Map<LanguageCode, String> descriptions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'DRAFT'")
    private DataStatus status = DataStatus.DRAFT;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<BrandDistilleryRelation> brandDistilleryRelations = new ArrayList<>();

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<Collection> collections = new ArrayList<>();

}
