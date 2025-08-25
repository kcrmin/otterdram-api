package com.otterdram.otterdram.domain.spirits.company;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.domain.spirits.brand.Brand;
import com.otterdram.otterdram.domain.spirits.distillery.Distillery;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Company Entity
 * <pre>
 * Table companies {
 *   id bigint [pk, increment]
 *   parent_company_id bigint [ref: > companies.id]
 *   company_logo varchar(255)
 *   company_name varchar(100) [not null, unique]
 *   translations jsonb [note: "다국어 지원 이름"]
 *   descriptions jsonb [note: "다국어 지원"]
 *   independent_bottler boolean
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

@Getter
@SuperBuilder
@Entity
@Table(name = "companies")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company extends SoftDeletable {

    @Id
    @SequenceGenerator(name = "company_seq", sequenceName = "company_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_company_id")
    private Company parentCompany;

    @Column(name = "company_logo", length = 255)
    private String companyLogo;

    @Column(name = "company_name", nullable = false, length = 100, unique = true)
    private String companyName;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "jsonb")
    private Map<LanguageCode, String> translations;

    @Type(JsonType.class)
    @Column(name = "descriptions", columnDefinition = "jsonb")
    private Map<LanguageCode, String> descriptions;

    @Column(name = "independent_bottler")
    private Boolean independentBottler;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'IN_REVIEW'")
    private DataStatus status = DataStatus.DRAFT;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "parentCompany", fetch = FetchType.LAZY)
    private List<Company> childCompanies = new ArrayList<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Distillery> distilleries = new ArrayList<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Brand> brands = new ArrayList<>();
}
