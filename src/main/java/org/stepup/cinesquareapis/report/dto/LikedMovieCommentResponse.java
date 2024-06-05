package org.stepup.cinesquareapis.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.stepup.cinesquareapis.report.entity.UserLikeComment;

@Data
@Schema(description = "좋아요한 코멘트 고유 키 응답 DTO")
public class LikedMovieCommentResponse {
    @Schema(description = "코멘트 고유 키", example = "12")
    private int commentId;

    public LikedMovieCommentResponse(UserLikeComment userLikeComment) {
        commentId = userLikeComment.getCommentId();
    }
}