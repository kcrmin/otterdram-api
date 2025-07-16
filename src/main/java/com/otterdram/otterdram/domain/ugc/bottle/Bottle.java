package com.otterdram.otterdram.domain.ugc.bottle;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.ugc.VolumeUnit;
import com.otterdram.otterdram.domain.spirits.release.Release;
import com.otterdram.otterdram.domain.ugc.shelf.Shelf;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

/** Bottle Entity
 * <pre>
 * Table bottles {
 *   id bigint [pk, increment]
 *   shelf_id bigint [ref: > shelves.id, not null]
 *   release_id bigint [ref: > releases.id, not null]
 *   note varchar(255)
 *   residual_volume smallint
 *   residual_volume_unit VolumeUnit [default: 'ML', not null]
 *   bottle_size smallint
 *   bottle_size_unit VolumeUnit [default: 'ML', not null]
 *   acquired_at timestamp
 *   opened boolean [not null, default: false]
 *   first_opened_at timestamp
 *   last_opened_at timestamp
 *   completed boolean [not null, default: false]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   updated_by bigint [ref: > users.id, not null]
 * }
 * </pre>
 */
@Entity
@Table(name = "bottle")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bottle extends SoftDeletable {

    @Id
    @SequenceGenerator(name = "bottle_seq", sequenceName = "bottle_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bottle_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "residual_volume")
    private Short residualVolume;

    @Enumerated(EnumType.STRING)
    @Column(name = "residual_volume_unit", nullable = false, columnDefinition = "varchar(10) default 'ML'")
    private VolumeUnit residualVolumeUnit = VolumeUnit.ML;

    @Column(name = "bottle_size")
    private Short bottleSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "bottle_size_unit", nullable = false, columnDefinition = "varchar(10) default 'ML'")
    private VolumeUnit bottleSizeUnit = VolumeUnit.ML;

    @Column(name = "acquired_at")
    private Instant acquiredAt;

    @Column(name = "opened", nullable = false, columnDefinition = "boolean default false")
    private Boolean opened = false;

    @Column(name = "first_opened_at")
    private Instant firstOpenedAt;

    @Column(name = "last_opened_at")
    private Instant lastOpenedAt;

    @Column(name = "completed", nullable = false, columnDefinition = "boolean default false")
    private Boolean completed = false;
}
