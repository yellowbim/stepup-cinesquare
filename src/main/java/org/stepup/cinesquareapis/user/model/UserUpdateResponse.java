package org.stepup.cinesquareapis.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.stepup.cinesquareapis.user.entity.User;

public record UserUpdateResponse(
        @Schema(description = "회원 정보 수정 성공 여부", example = "true")
        boolean result,
        @Schema(description = "회원 이름", example = "콜라곰")
        String name,
        @Schema(description = "회원 나이", example = "30")
        String nickname
) {
    public static UserUpdateResponse of(boolean result, User user) {
        return new UserUpdateResponse(result, user.getName(), user.getNickname());
    }
}
