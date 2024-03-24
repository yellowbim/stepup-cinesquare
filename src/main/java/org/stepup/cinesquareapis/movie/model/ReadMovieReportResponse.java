package org.stepup.cinesquareapis.movie.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.stepup.cinesquareapis.movie.entity.MovieReport;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class ReadMovieReportResponse {

    private Integer userId;
    private Integer movieId;
    private int status;
    private int score;
    private String comment;
    private LocalDateTime created;
    private LocalDateTime updated;

    public ReadMovieReportResponse(MovieReport movieReport) {
        userId = movieReport.getUserId();
        movieId = movieReport.getMovieId();
        score = movieReport.getScore();
        status = movieReport.getStatus();
        comment = movieReport.getComment();
        created = movieReport.getCreated();
        updated = movieReport.getUpdated();
    }

}
