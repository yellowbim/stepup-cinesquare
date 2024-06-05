package org.stepup.cinesquareapis.report.dto;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.MovieComment;

@Data
public class MovieCommentSaveRequest {
    private String content;

    public MovieComment toEntity(int movieId, int userId) {
        return new MovieComment().builder()
                .content(content)
                .userId(userId)
                .movieId(movieId)
                .build();
    }
}
