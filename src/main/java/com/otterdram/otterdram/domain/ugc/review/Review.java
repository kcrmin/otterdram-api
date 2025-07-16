package com.otterdram.otterdram.domain.ugc.review;

import com.otterdram.otterdram.common.audit.superclass.AuthorModifiable;
import com.otterdram.otterdram.common.enums.ugc.*;
import com.otterdram.otterdram.common.enums.converter.PeriodConverter;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.common.enums.common.Privacy;
import com.otterdram.otterdram.domain.spirits.release.Release;
import com.otterdram.otterdram.domain.ugc.vial.Vial;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Period;

/** Review Entity
 * <pre>
 * Table reviews {
 *   review_id bigint [pk, increment]
 *   release_id bigint [ref: > releases.id, not null]
 *   vial_id bigint [ref: > vials.id]
 *   privacy Privacy [not null, default: 'PUBLIC']
 *   language_code LanguageCode [not null]
 *   blind_tasting boolean [not null, default: false]
 *   serving_style ServingStyle [not null]
 *   serving_size smallint [not null]
 *   serving_size_unit VolumeUnit [not null, default: 'UNKNOWN']
 *   breathed_for interval [note: "첫 개봉 후 지난 시간 (last_opened_at - first_opened_at)"]
 *   tier Tier [not null]
 *   score decimal(4, 1)
 *   alcohol_presence AlcoholPresence [not null]
 *   complexity Complexity [not null]
 *   body_intensity BodyIntensity [not null]
 *   finish_length FinishLength [not null]
 *   balance Balance [not null]
 *   overall_description text [note: "종합 평가"]
 *   nose_description text [note: "향"]
 *   palate_description text [note: "맛"]
 *   finish_description text [note: "피니시"]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 *   modified_at timestamp
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   updated_by bigint [ref: > users.id, not null]
 *   deleted_at timestamp
 *   deleted_by bigint [ref: > users.id]
 * }
 * </pre>
 */

@Entity
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends AuthorModifiable {

    @Id
    @SequenceGenerator(name = "review_id_seq", sequenceName = "review_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vial_id")
    private Vial vial;

    // TODO: Privacy of the review, get value from User's settings
    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", nullable = false, columnDefinition = "varchar(10) default 'PUBLIC'")
    private Privacy privacy = Privacy.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_code", nullable = false, columnDefinition = "varchar(10) default 'EN'")
    private LanguageCode languageCode = LanguageCode.EN;

    @Column(name = "blind_tasting", nullable = false, columnDefinition = "boolean default false")
    private boolean blindTasting = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "serving_style", nullable = false, columnDefinition = "varchar(50) default 'NEAT'")
    private ServingStyle servingStyle = ServingStyle.NEAT;

    @Column(name = "serving_size")
    private Short servingSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "serving_size_unit", nullable = false, columnDefinition = "varchar(10) default 'UNKNOWN'")
    private VolumeUnit servingSizeUnit = VolumeUnit.UNKNOWN;

    @Column(name = "breathed_for")
    @Convert(converter = PeriodConverter.class)
    private Period breathedFor;

    // TODO: Automatically set it based on the score
    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false, length = 50)
    private Tier tier;

    @Column(name = "score", precision = 4, scale = 1)
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(name = "alcohol_presence", nullable = false, length = 50)
    private AlcoholPresence alcoholPresence;

    @Enumerated(EnumType.STRING)
    @Column(name = "complexity", nullable = false, length = 50)
    private Complexity complexity;

    @Enumerated(EnumType.STRING)
    @Column(name = "body_intensity", nullable = false, length = 50)
    private BodyIntensity bodyIntensity;

    @Enumerated(EnumType.STRING)
    @Column(name = "finish_length", nullable = false, length = 50)
    private FinishLength finishLength;

    @Enumerated(EnumType.STRING)
    @Column(name = "balance", nullable = false, length = 50)
    private Balance balance;

    @Column(name = "overall_description", columnDefinition = "text")
    private String overallDescription;

    @Column(name = "nose_description", columnDefinition = "text")
    private String noseDescription;

    @Column(name = "palate_description", columnDefinition = "text")
    private String palateDescription;

    @Column(name = "finish_description", columnDefinition = "text")
    private String finishDescription;
}