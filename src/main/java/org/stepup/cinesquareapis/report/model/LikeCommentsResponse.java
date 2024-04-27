package org.stepup.cinesquareapis.report.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.CommentSummary;

@Data
@Schema(description = "사용자가 좋아요한 코멘트 목록 응담 DTO")
public class LikeCommentsResponse {
    @Schema(description = "코멘트 남긴 사용자 고유 키", example = "1")
    private int userId; // 조회한 사용자
    @Schema(description = "코멘트 고유 키", example = "1")
    private int commentId; // 조회한 사용자
    @Schema(description = "회원 별칭", example = "콜라곰")
    private String nickname; // 조회한 사용자
    @Schema(description = "영화 고유 키", example = "1")
    private int movieId; // 조회한 사용자
    @Schema(description = "코멘트 내용", example = "영화 너무 재밌어요!!")
    private String content; // 조회한 사용자
    @Schema(description = "코멘트 좋아요 개수", example = "55")
    private int like; // 조회한 사용자
    @Schema(description = "코멘트 답변 개수", example = "11")
    private int replyCount; // 조회한 사용자
    @Schema(description = "코멘트 사용자가 남긴 별점", example = "4")
    private Double score; // 조회한 사용자

    // 생성자
    public LikeCommentsResponse(CommentSummary comment) {
        userId = comment.getUserId();
        nickname = comment.getNickname();
        movieId = comment.getMovieId();
        commentId = comment.getCommentId();
        content = comment.getContent();
        like = comment.getLike();
        replyCount = comment.getReplyCount();
        score = comment.getScore();
    }


}
