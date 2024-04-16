package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.UserScore;

import java.time.LocalDateTime;

@Data
public class UserScoreRequest {
    private Double score;

    public UserScore toEntity(int userId, int movieId) {
        return new UserScore().builder()
                .userId(userId)
                .movieId(movieId)
                .score(score)
                .build();
    }
}
