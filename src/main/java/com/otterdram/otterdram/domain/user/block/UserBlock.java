package com.otterdram.otterdram.domain.user.block;

import com.otterdram.otterdram.common.audit.superclass.timestamp.CreatableTimestamp;
import com.otterdram.otterdram.domain.user.user.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;

/** UserBlock Entity
 * Table user_blocks {
 *   id bigint [pk, increment]
 *   blocker_id bigint [ref: > users.id, not null]
 *   blocked_id bigint [ref: > users.id, not null]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 * }
 */

@Entity
@Table(name = "user_blocks")
public class UserBlock extends CreatableTimestamp {

    @Id
    @SequenceGenerator(name = "user_block_seq", sequenceName = "user_block_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_block_seq")
    private Long id;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked;

}
