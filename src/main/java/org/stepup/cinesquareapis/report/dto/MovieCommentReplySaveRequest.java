package org.stepup.cinesquareapis.report.dto;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentReply;

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
