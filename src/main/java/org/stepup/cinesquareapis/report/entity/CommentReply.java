package org.stepup.cinesquareapis.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tb_movie_comment_reply")
public class CommentReply {
    @Id
    @Column(nullable = false, name = "reply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer replyId;

    @Column(name = "comment_id")
    private Integer commentId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "content", length = 1000)
    private String content;

    @Column(name = "like")
    private int like;

    @Column(name = "movie_id", nullable = false)
    private Integer movieId;

    @CreationTimestamp
    @Column(name = "created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated;
}
