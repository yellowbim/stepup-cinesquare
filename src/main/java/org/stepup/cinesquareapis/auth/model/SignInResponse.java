package org.stepup.cinesquareapis.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.stepup.cinesquareapis.auth.jwt.RoleType;

public record SignInResponse(
        @Schema(description = "회원 이름", example = "콜라곰")
        String name,
        @Schema(description = "회원 유형", example = "USER")
        RoleType type,

        String accessToken
) {
}