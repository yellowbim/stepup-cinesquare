package org.stepup.cinesquareapis.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserLikeCommentKey.class)
@Table(name = "tb_user_like_comment")
public class UserLikeComment {

    @Id
    @Column(nullable = false)
    private Integer userId;

    @Id
    @Column(name = "comment_id")
    private Integer commentId;

    @Id
    @Column(name = "movie_id", nullable = false)
    private Integer movieId;

    @CreationTimestamp
    @Column(name = "created", updatable = false)
    private LocalDateTime created;
}
