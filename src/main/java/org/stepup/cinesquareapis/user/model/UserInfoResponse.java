package org.stepup.cinesquareapis.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.stepup.cinesquareapis.user.enums.RoleType;
import org.stepup.cinesquareapis.user.entity.User;

import java.time.LocalDateTime;

public record UserInfoResponse(
        @Schema(description = "회원 고유키", example = "c0a80121-7aeb-4b4b-8b0a-6b1c032f0e4a")
        Integer userId,
        @Schema(description = "회원 아이디", example = "colabear754")
        String account,
        @Schema(description = "회원 이름", example = "콜라곰")
        String name,
        @Schema(description = "회원 나이", example = "30")
        String nickname,
        @Schema(description = "회원 타입", example = "USER")
        RoleType type,
        @Schema(description = "회원 생성일", example = "2023-05-11T15:00:00")
        LocalDateTime created
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getUserId(),
                user.getAccount(),
                user.getName(),
                user.getNickname(),
                user.getType(),
                user.getCreated()
        );
    }
}
