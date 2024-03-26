package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.UserScore;

import java.time.LocalDateTime;

@Data
public class UserScoreRequest {
    private int userId;
    private int movieId;
    private Double score;
    private LocalDateTime created;
    private LocalDateTime updated;

    public UserScore toEntity() {
        return new UserScore().builder()
                .userId(userId)
                .movieId(movieId)
                .score(score)
                .build();
    }
}
