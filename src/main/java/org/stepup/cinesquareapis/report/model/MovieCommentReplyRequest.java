package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentReply;

import java.time.LocalDateTime;

@Data
public class MovieCommentReplyRequest {
    private int replyId;
    private int commentId;
    private int userId;
    private String content;
    private int like;
    private LocalDateTime created;
    private LocalDateTime updated;

    public CommentReply toEntity() {
        return new CommentReply().builder()
                .replyId(replyId)
                .commentId(commentId)
                .userId(userId)
                .content(content)
                .like(like)
                .build();
    }
}
