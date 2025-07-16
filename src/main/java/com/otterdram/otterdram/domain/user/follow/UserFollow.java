package com.otterdram.otterdram.domain.user.follow;

import com.otterdram.otterdram.common.audit.superclass.timestamp.CreatableTimestamp;
import com.otterdram.otterdram.common.enums.user.FollowStatus;
import com.otterdram.otterdram.domain.user.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;

/**
 * UserFollow Entity
 * <pre>
 * Table user_follows {
 *   id bigint [pk, increment]
 *   follower_id bigint [ref: > users.id, not null]
 *   followee_id bigint [ref: > users.id, not null]
 *   status FollowStatus [not null, default: `PENDING`]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 * }
 * </pre>
 */
@Entity
@Table(name = "user_follows", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFollow extends CreatableTimestamp {

    @Id
    @SequenceGenerator(name = "user_follow_seq", sequenceName = "user_follow_sequence")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE, generator = "user_follow_seq")
    private Long id;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'PENDING'")
    private FollowStatus status = FollowStatus.PENDING;

}
