package org.stepup.cinesquareapis.report.dto;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.Comment;

@Data
public class MovieCommentSaveRequest {
    private String content;

    public Comment toEntity(int movieId, int userId) {
        return new Comment().builder()
                .content(content)
                .userId(userId)
                .movieId(movieId)
                .build();
    }
}
