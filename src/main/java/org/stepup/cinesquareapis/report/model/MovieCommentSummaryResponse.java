package org.stepup.cinesquareapis.report.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentSummary;

@Data
@Schema(description = "코멘트 목록 + 별점 응답 DTO")
public class MovieCommentSummaryResponse {
    @Schema(description = "코멘트 고유 키", example = "12")
    private int commentId;
    @Schema(description = "영화 고유 키", example = "20")
    private int movieId;
    @Schema(description = "사용자 고유 키", example = "11")
    private int userId;
    @Schema(description = "코멘트 내용", example = "영화 너무 재밌어요!!")
    private String content;
    @Schema(description = "코멘트 작성자가 남긴 별점", example = "3.5")
    private Double score;
    @Schema(description = "사용자 닉네임", example = "콜라곰")
    private String nickname;
    @Schema(description = "코멘트 좋아요 수", example = "12")
    private int like;
    @Schema(description = "코멘트 답글 수", example = "2")
    private int replyCount;

    public MovieCommentSummaryResponse(CommentSummary commentSummary) {
        commentId = commentSummary.getCommentId();
        movieId = commentSummary.getMovieId();
        userId = commentSummary.getUserId();
        content = commentSummary.getContent();
        score = commentSummary.getScore();
        nickname = commentSummary.getNickname();
        like = commentSummary.getLike();
        replyCount = commentSummary.getReplyCount();
    }

}
