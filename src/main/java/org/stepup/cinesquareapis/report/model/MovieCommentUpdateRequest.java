package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.Comment;

@Data
public class MovieCommentUpdateRequest {
    private String content;

    public Comment toEntity(int commentId, int movieId, Integer userId) {
        return new Comment().builder()
                .commentId(commentId)
                .content(content)
                .userId(userId)
                .movieId(movieId)
                .build();
    }
}
