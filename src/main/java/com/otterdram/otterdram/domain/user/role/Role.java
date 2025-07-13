package com.otterdram.otterdram.domain.user.role;

import com.otterdram.otterdram.common.audit.auditable.Updatable;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/** Role Entity
 * <pre>
 * Table roles {
 *   id bigint [pk, increment]
 *   role_name varchar(50) [unique, not null, note: "ROLE_REVIEWER, ROLE_EDITOR, ROLE_ADMIN, ROLE_USER"]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   updated_by bigint [ref: > users.id, not null]
 * }
 * </pre>
 */
@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(columnNames = {"role_name"}))
public class Role extends Updatable {

    @Id
    @SequenceGenerator(name = "role_seq", sequenceName = "role_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    // UserRole relationship
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserRole> userRoles = new ArrayList<>();

}
