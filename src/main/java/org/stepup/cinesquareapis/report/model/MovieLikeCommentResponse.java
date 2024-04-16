package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentReply;
import org.stepup.cinesquareapis.report.entity.UserLikeComment;

import java.time.LocalDateTime;

@Data
public class MovieLikeCommentResponse {
    private int commentId;
    private LocalDateTime created;

    public MovieLikeCommentResponse(UserLikeComment userLikeComment) {
        commentId = userLikeComment.getCommentId();
        created = userLikeComment.getCreated();
    }
}
