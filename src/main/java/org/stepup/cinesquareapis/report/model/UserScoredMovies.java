package org.stepup.cinesquareapis.report.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마이페이지 평가한 영화 목록 응답 DTO")
public interface UserScoredMovies {
    @Schema(description = "영화 고유 키", example = "12")
    int getMovieId();
    @Schema(description = "영화 제목", example = "범죄도시4")
    String getTitle();
    @Schema(description = "평가한 별점", example = "0.5 ~ 5")
    Double getScore();
}
