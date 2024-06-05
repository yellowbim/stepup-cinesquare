package org.stepup.cinesquareapis.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignUpRequest(
        @Schema(description = "계정", example = "soomin@gmail.com")
        String account,
        @Schema(description = "비밀번호", example = "qwe123")
        String password,
        @Schema(description = "이름", example = "조수민")
        String name,
        @Schema(description = "닉네임", example = "쑴")
        String nickname
) {
}
