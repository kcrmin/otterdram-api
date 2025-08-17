package com.otterdram.otterdram.domain.spirits.release;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.common.enums.spirits.AgeStatementType;
import com.otterdram.otterdram.common.enums.spirits.BottlingFormatType;
import com.otterdram.otterdram.common.enums.spirits.BottlingStrengthType;
import com.otterdram.otterdram.common.enums.spirits.PeatLevel;
import com.otterdram.otterdram.domain.spirits.model.Model;
import com.otterdram.otterdram.domain.spirits.relation.DistilleryReleaseRelation;
import com.otterdram.otterdram.domain.spirits.relation.ReleaseCaskRelation;
import com.otterdram.otterdram.domain.ugc.bottle.Bottle;
import com.otterdram.otterdram.domain.ugc.review.Review;
import com.otterdram.otterdram.domain.ugc.vial.Vial;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Release Entity
 * <pre>
 * Table releases {
 *   id bigint [pk, increment]
 *   model_id bigint [ref: > models.id, not null]
 *   release_image varchar(255)
 *   release_name varchar(100) [not null]
 *   translations jsonb [note: "다국어 지원 이름"]
 *   descriptions jsonb [note: "다국어 지원"]
 *   age_statement_type AgeStatementType [not null, default: 'UNKNOWN']
 *   stated_age smallint [note: "age_type이 'STATED'일 때만 값이 있음"]
 *   distilled_on date
 *   bottled_on date
 *   bottling_strength_type BottlingStrengthType [not null, default: 'STANDARD']
 *   abv decimal(5,2) [not null]
 *   limited_edition boolean [note: "한정판: NULL은 UNKNOWN"]
 *   released_bottles varchar(32) [note: "총 발매병수(예: '2000', '≈500' 등)"]
 *   bottling_format_type BottlingFormatType [not null, default: 'UNKNOWN']
 *   chill_filtered boolean [note: "NULL은 UNKNOWN"]
 *   natural_color boolean [note: "NULL은 UNKNOWN"]
 *   peat_level PeatLevel [not null, default: 'UNKNOWN']
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
@Table(name = "release")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Release extends SoftDeletable {

    @Id
    @SequenceGenerator(name = "release_seq", sequenceName = "release_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "release_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @Column(name = "release_image", length = 255)
    private String releaseImage;

    @Column(name = "release_name", nullable = false, length = 100)
    private String releaseName;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "jsonb")
    private Map<LanguageCode, String> translations;

    @Type(JsonType.class)
    @Column(name = "descriptions", columnDefinition = "jsonb")
    private Map<LanguageCode, String> descriptions;

    // =========================== Age and Bottling Information ===========================
    @Enumerated(EnumType.STRING)
    @Column(name = "age_statement_type", columnDefinition = "varchar(20) default 'UNKNOWN'")
    private AgeStatementType ageStatementType = AgeStatementType.UNKNOWN;

    @Column(name = "stated_age", columnDefinition = "smallint")
    private Short statedAge;

    @Column(name = "distilled_on")
    private LocalDate distilledOn;

    @Column(name = "bottled_on")
    private LocalDate bottledOn;

    // =========================== Bottling Strength Information ===========================
    @Enumerated(EnumType.STRING)
    @Column(name = "bottling_strength_type", nullable = false, columnDefinition = "varchar(20) default 'STANDARD'")
    private BottlingStrengthType bottlingStrengthType = BottlingStrengthType.STANDARD;

    @Column(name = "abv", nullable = false, precision = 5, scale = 2)
    private Double abv;

    // =========================== Release Characteristics ===========================
    @Column(name = "limited_edition", columnDefinition = "boolean")
    private Boolean limitedEdition; // NULL indicates UNKNOWN

    @Column(name = "released_bottles", length = 32)
    private String releasedBottles; // Total released bottles (e.g., '2000', '≈500')

    // =========================== Cask and Filtering Information ===========================
    @Enumerated(EnumType.STRING)
    @Column(name = "bottling_format_type", nullable = false, columnDefinition = "varchar(20) default 'UNKNOWN'")
    private BottlingFormatType bottlingFormatType = BottlingFormatType.UNKNOWN;

    @Column(name = "chill_filtered", columnDefinition = "boolean")
    private Boolean chillFiltered; // NULL indicates UNKNOWN

    @Column(name = "natural_color", columnDefinition = "boolean")
    private Boolean naturalColor; // NULL indicates UNKNOWN

    // =========================== Peat Level Information ===========================
    @Enumerated(EnumType.STRING)
    @Column(name = "peat_level", nullable = false, columnDefinition = "varchar(20) default 'UNKNOWN'")
    private PeatLevel peatLevel = PeatLevel.UNKNOWN;

    // =========================== Status Information ===========================
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'IN_REVIEW'")
    private DataStatus status = DataStatus.IN_REVIEW;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    private List<DistilleryReleaseRelation> distilleryReleaseRelations = new ArrayList<>();

    @OneToMany(mappedBy = "release", fetch = FetchType.LAZY)
    private List<ReleaseCaskRelation> releaseCaskRelations = new ArrayList<>();

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Bottle> bottles = new ArrayList<>();

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Vial> vials = new ArrayList<>();

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
}
