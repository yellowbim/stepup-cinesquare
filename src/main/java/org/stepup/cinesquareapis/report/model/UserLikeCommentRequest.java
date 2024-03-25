package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.UserLikeComment;
import org.stepup.cinesquareapis.report.entity.UserScore;

import java.time.LocalDateTime;

@Data
public class UserLikeCommentRequest {
    private int userId;
    private int commentId;
    private LocalDateTime created;
    private LocalDateTime updated;

    public UserLikeComment toEntity() {
        return new UserLikeComment().builder()
                .userId(userId)
                .commentId(commentId)
                .build();
    }
}
