package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.UserScore;

@Data
public class UserScoreRequest {
    private float score;

    public UserScore toEntity(int userId, int movieId) {
        return new UserScore().builder()
                .userId(userId)
                .movieId(movieId)
                .score(score)
                .build();
    }
}
