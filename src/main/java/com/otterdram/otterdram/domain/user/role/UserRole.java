package com.otterdram.otterdram.domain.user.role;

import com.otterdram.otterdram.common.audit.timestamp.CreatableTimestamp;
import com.otterdram.otterdram.domain.user.user.User;
import jakarta.persistence.*;

/** UserRole Entity
 * <pre>
 * Table user_roles {
 *   id bigint [pk, increment]
 *   user_id bigint [ref: > users.id, not null]
 *   role_id bigint [ref: > roles.id, not null]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`, note: "ROLE_USER: created_at, ROLE_ADMIN: assigned_at"]
 *   created_by bigint [ref: > users.id, not null, note: "ROLE_USER: created_by, ROLE_ADMIN: assigned_by"]
 * }
 * </pre>
 */

@Entity
@Table(name = "user_roles", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
public class UserRole extends CreatableTimestamp {

    @Id
    @SequenceGenerator(name= "user_role_seq", sequenceName = "user_role_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_role_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

}
