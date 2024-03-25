package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.UserStatus;

import java.time.LocalDateTime;

@Data
public class UserStatusRequest {
    private int userId;
    private int movieId;
    private int status;
    private LocalDateTime created;
    private LocalDateTime updated;

    public UserStatus toEntity() {
        return new UserStatus().builder()
                .userId(userId)
                .movieId(movieId)
                .status(status)
                .build();
    }
}
