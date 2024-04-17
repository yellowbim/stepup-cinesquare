package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;
import org.stepup.cinesquareapis.report.entity.UserScore;

public interface UserScoredResponse {
    Integer getMovieId();
    String getTitle();
    Double getScore();

}
