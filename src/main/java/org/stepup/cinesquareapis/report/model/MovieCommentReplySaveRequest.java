package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentReply;

import java.time.LocalDateTime;

@Data
public class MovieCommentReplySaveRequest {
    private int userId;
    private String content;
    private int like;

    public CommentReply toEntity(Integer commentId, Integer movieId) {
        return new CommentReply().builder()
                .commentId(commentId)
                .userId(userId)
                .content(content)
                .like(like)
                .movieId(movieId)
                .build();
    }
}
