package org.stepup.cinesquareapis.report.dto;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentReply;

@Data
public class MovieCommentReplyUpdateRequest {
    private String content;

    public CommentReply toEntity(int replyId, int commentId, int movieId, int userId) {
        return new CommentReply().builder()
                .replyId(replyId)
                .commentId(commentId)
                .userId(userId)
                .content(content)
                .movieId(movieId)
                .build();
    }
}
