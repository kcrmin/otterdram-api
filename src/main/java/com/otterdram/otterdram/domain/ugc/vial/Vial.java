package com.otterdram.otterdram.domain.ugc.vial;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.ugc.VolumeUnit;
import com.otterdram.otterdram.domain.spirits.release.Release;
import com.otterdram.otterdram.domain.ugc.shelf.Shelf;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

/** Vial Entity
 * <pre>
 * Table vials {
 *   id bigint [pk, increment]
 *   shelf_id bigint [ref: > shelves.id, not null]
 *   release_id bigint [ref: > releases.id, not null]
 *   vial_name varchar(100) [not null]
 *   is_blind boolean [not null, default: false]
 *   note varchar(255)
 *   vial_size smallint
 *   vial_size_unit VolumeUnit [default: 'ML', not null]
 *   acquired_at timestamp
 *   breathed_for interval [note: "첫 개봉 후 지난 시간 (last_opened_at - first_opened_at)"]
 *   completed boolean [not null, default: false]
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
@Table(name = "vials")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vial extends SoftDeletable {

    @Id
    @SequenceGenerator(name = "vial_seq", sequenceName = "vial_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vial_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    @Column(name = "vial_name", nullable = false, length = 100)
    private String vialName;

    @Column(name = "is_blind", nullable = false, columnDefinition = "boolean default false")
    private boolean isBlind = false;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "vial_size")
    private Short vialSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "vial_size_unit", nullable = false, columnDefinition = "varchar(10) default 'ML'")
    private VolumeUnit vialSizeUnit = VolumeUnit.ML;

    @Column(name = "acquired_at")
    private Instant acquiredAt;

    @Column(name = "breathed_for")
    private Instant breathedFor;

    @Column(name = "completed", nullable = false, columnDefinition = "boolean default false")
    private boolean completed = false;
}
