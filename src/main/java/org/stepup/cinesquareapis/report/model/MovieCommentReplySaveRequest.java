package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentReply;

import java.time.LocalDateTime;

@Data
public class MovieCommentReplySaveRequest {
    private String content;

    public CommentReply toEntity(Integer commentId, Integer movieId, Integer userId) {
        return new CommentReply().builder()
                .commentId(commentId)
                .userId(userId)
                .content(content)
                .movieId(movieId)
                .build();
    }
}
