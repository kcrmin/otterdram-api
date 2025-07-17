package com.otterdram.otterdram.domain.ugc.shelf;

import com.otterdram.otterdram.common.audit.superclass.timestamp.SoftDeletableTimestamp;
import com.otterdram.otterdram.common.enums.common.Privacy;
import com.otterdram.otterdram.domain.ugc.bottle.Bottle;
import com.otterdram.otterdram.domain.ugc.vial.Vial;
import com.otterdram.otterdram.domain.user.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Shelf Entity
 * <pre>
 * Table shelves {
 *   id bigint [pk, increment]
 *   owner_id bigint [not null, ref: > users.id]
 *   privacy Privacy [not null, default: 'PUBLIC']
 *   shelf_name varchar(100) [not null]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   deleted_at timestamp
 * }
 * </pre>
 */
@Entity
@Table(name = "shelves")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shelf extends SoftDeletableTimestamp {

    @Id
    @SequenceGenerator(name = "shelf_seq", sequenceName = "shelf_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shelf_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", nullable = false, columnDefinition = "varchar(10) default 'PUBLIC'")
    private Privacy privacy = Privacy.PUBLIC;

    @Column(name = "shelf_name", nullable = false, length = 100)
    private String shelfName;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Bottle> bottles = new ArrayList<>();

    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Vial> vials = new ArrayList<>();
}
