package org.stepup.cinesquareapis.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마이페이지 별점 분포 응답 DTO")
public interface UserMovieRating {
    @Schema(description = "평가한 별점", example = "0.5")
    Double getScore();
    @Schema(description = "개수", example = "15")
    int getCount();
}
