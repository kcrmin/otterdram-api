package com.otterdram.otterdram.domain.spirits.company;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.DataStatus;
import com.otterdram.otterdram.common.enums.LanguageCode;
import com.otterdram.otterdram.domain.spirits.brand.Brand;
import com.otterdram.otterdram.domain.spirits.distillery.Distillery;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
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
 *   translations text [note: "다국어 지원 이름"]
 *   descriptions text [note: "다국어 지원"]
 *   independent_bottler boolean [not null, default: false]
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
@Table(name = "companies")
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
    @Column(name = "translations", columnDefinition = "json")
    private Map<LanguageCode, String> translations;

    @Type(JsonType.class)
    @Column(name = "descriptions", columnDefinition = "json")
    private Map<LanguageCode, String> descriptions;

    @Column(name = "independent_bottler", nullable = false, columnDefinition = "boolean default false")
    private boolean independentBottler = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'DRAFT'")
    private DataStatus status = DataStatus.DRAFT;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "parentCompany", fetch = FetchType.LAZY)
    private List<Company> childCompanies = new ArrayList<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Distillery> distilleries = new ArrayList<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Brand> brands = new ArrayList<>();
}
