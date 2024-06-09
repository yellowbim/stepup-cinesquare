package org.stepup.cinesquareapis.report.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tb_comment_reply")
public class CommentReply {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer replyId;

    @Column(nullable = false)
    private Integer commentId;

    @Column(nullable = false)
    private Integer userId;

    @NotBlank()
    @Column(length = 1000, nullable = false)
    private String content;

    @Column(name = "like")
    private int like;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;
}