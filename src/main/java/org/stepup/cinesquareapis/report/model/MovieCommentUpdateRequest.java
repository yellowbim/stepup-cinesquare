package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.Comment;

@Data
public class MovieCommentUpdateRequest {
    private String content;
    private int replyCount;
    private int like;
    private int userId;

    public Comment toEntity(int commentId, int movieId) {
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
