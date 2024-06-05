package org.stepup.cinesquareapis.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.stepup.cinesquareapis.report.entity.MovieComment;

import java.time.LocalDateTime;

@Data
@Schema(description = "코멘트 목록 응답 DTO")
public class MovieCommentResponse {
    @Schema(description = "코멘트 고유 키", example = "12")
    private Integer commentId;

    @Schema(description = "코멘트 내용", example = "영화 너무 재밌어요!!")
    private String content;

    @Schema(description = "코멘트 좋아요 수", example = "55")
    private int like;

    @Schema(description = "코멘트 답글 수", example = "11")
    private int replyCount;

    @Schema(description = "생성일", example = "2023-05-11T15:00:00")
    private LocalDateTime created;

    @Schema(description = "수정일", example = "2023-05-11T15:00:00")
    private LocalDateTime updated;

    public MovieCommentResponse() {
    }

    // MovieComment 객체로 MovieCommentResponse를 생성
    public MovieCommentResponse(MovieComment movieComment) {
        this.commentId = movieComment.getCommentId();
        this.content = movieComment.getContent();
        this.like = movieComment.getLike();
        this.replyCount = movieComment.getReplyCount();
        this.created = movieComment.getUpdated();
        this.updated = movieComment.getUpdated();
    }
}