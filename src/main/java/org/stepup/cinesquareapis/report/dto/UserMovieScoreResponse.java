package org.stepup.cinesquareapis.report.dto;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.UserScore;

import java.time.LocalDateTime;

@Data
public class UserMovieScoreResponse {
    public int movieId;

    public Float score;

    public LocalDateTime updated;

    public UserMovieScoreResponse(int movieId) {
        this.movieId = movieId;
        score = null;
        updated = null;
    }

    public UserMovieScoreResponse(UserScore userScore) {
        movieId = userScore.getMovieId();
        score = userScore.getScore();
        updated = userScore.getUpdated();
    }
}
