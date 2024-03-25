package org.stepup.cinesquareapis.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tb_movie_comment_summary")
public class CommentSummary {

    @Id
    @Column(nullable = false)
    private Integer commentId;

//    @Id
    @Column(nullable = false)
    private Integer movieId;

//    @Id
    @Column(nullable = false)
    private Integer userId;

    @Column(name = "content", length = 1000)
    private String content;

    @ColumnDefault("0")
    @Column(name = "score")
    private Integer score;

    @Column(name = "nickname")
    private String nickname;

    @ColumnDefault("0")
    @Column(name = "\"like\"") // 예약어라 제외를 시켜주는 방법
    private Integer like;

    @ColumnDefault("0")
    @Column(name = "reply_count")
    private Integer replyCount;

    @CreationTimestamp
    @Column(name = "created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated;
}
