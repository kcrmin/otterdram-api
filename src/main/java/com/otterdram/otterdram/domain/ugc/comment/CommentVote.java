package com.otterdram.otterdram.domain.ugc.comment;

import com.otterdram.otterdram.common.audit.superclass.Creatable;
import com.otterdram.otterdram.common.enums.ugc.VoteType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Comment Vote Entity
 * <pre>
 * Table comment_votes {
 *   id bigint [pk, increment]
 *   entity_comment_id bigint [ref: > entity_comments.id]
 *   vote_type VoteType [not null]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 *   indexes {
 *     (entity_comment_id, created_by) [unique]
 *   }
 * }
 * </pre>
 */

@Entity
@Table(name = "comment_votes", uniqueConstraints = @UniqueConstraint(columnNames = {"entity_comment_id", "created_by"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentVote extends Creatable {

    @Id
    @SequenceGenerator(name = "comment_vote_seq", sequenceName = "comment_vote_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_vote_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_comment_id", nullable = false)
    private EntityComment entityComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false, length = 10, columnDefinition = "varchar(10)")
    private VoteType voteType;
}
