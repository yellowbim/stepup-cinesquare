package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.Comment;

@Data
public class MovieCommentSaveRequest {
    private String content;
    private int userId;

    public Comment toEntity(int movieId) {
        return new Comment().builder()
                .content(content)
                .userId(userId)
                .movieId(movieId)
                .build();
    }
}
