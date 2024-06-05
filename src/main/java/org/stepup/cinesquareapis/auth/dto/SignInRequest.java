package org.stepup.cinesquareapis.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignInRequest(
        @Schema(description = "계정", example = "soomin@gmail.com")
        String account,
        @Schema(description = "비밀번호", example = "qwe123")
        String password
) {
}