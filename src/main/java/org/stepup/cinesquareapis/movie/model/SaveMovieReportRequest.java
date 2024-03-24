package org.stepup.cinesquareapis.movie.model;

import jakarta.persistence.IdClass;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.stepup.cinesquareapis.movie.entity.MovieReport;

@Data
public class SaveMovieReportRequest {

    Integer userId;
    Integer movieId;
    int status;
    String comment;
    int score;

    public MovieReport toEntity() {
        return MovieReport.builder()
                .movieId(movieId)
                .userId(userId)
                .status(status)
                .score(score)
                .comment(comment)
                .build();
    }
}
