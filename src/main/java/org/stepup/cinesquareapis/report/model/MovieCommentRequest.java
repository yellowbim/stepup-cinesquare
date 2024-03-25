package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.UserLikeComment;

import java.time.LocalDateTime;

@Data
public class MovieCommentRequest {
    private Integer commentId;
    private String content;
    private int replyCount;
    private int like;
    private int userId;
    private int movieId;
    private LocalDateTime created;
    private LocalDateTime updated;

    public Comment toEntity() {
        return new Comment().builder()
                .commentId(commentId)
                .content(content)
                .replyCount(replyCount)
                .like(like)
                .userId(userId)
                .movieId(movieId)
                .build();
    }
}
