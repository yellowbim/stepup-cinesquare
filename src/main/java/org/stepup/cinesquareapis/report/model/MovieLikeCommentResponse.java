package org.stepup.cinesquareapis.report.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.stepup.cinesquareapis.report.entity.UserLikeComment;

import java.time.LocalDateTime;

@Data
@Schema(description = "좋아요한 코멘트 고유 키 응답 DTO")
public class MovieLikeCommentResponse {
    @Schema(description = "코멘트 고유 키", example = "12")
    private int commentId;
    @Schema(description = "수정일", example = "2023-05-11T15:00:00")
    private LocalDateTime created;

    public MovieLikeCommentResponse(UserLikeComment userLikeComment) {
        commentId = userLikeComment.getCommentId();
        created = userLikeComment.getCreated();
    }
}
