package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentReply;

import java.time.LocalDateTime;

@Data
public class MovieCommentReplyUpdateRequest {
    private int userId;
    private String content;
    private int like;

    public CommentReply toEntity(int replyId, int commentId, int movieId) {
        return new CommentReply().builder()
                .replyId(replyId)
                .commentId(commentId)
                .userId(userId)
                .content(content)
                .like(like)
                .movieId(movieId)
                .build();
    }
}
