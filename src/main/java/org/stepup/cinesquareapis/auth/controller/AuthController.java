package org.stepup.cinesquareapis.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.auth.model.SignInRequest;
import org.stepup.cinesquareapis.auth.model.SignInResponse;
import org.stepup.cinesquareapis.auth.model.SignUpRequest;
import org.stepup.cinesquareapis.auth.model.SignUpResponse;
import org.stepup.cinesquareapis.auth.service.AuthService;

@RequiredArgsConstructor
@Tag(name = "1 auth", description = "Auth API")
@RequestMapping("api/auth")
@RestController
public class AuthController {
    private final AuthService authService;
    @PostMapping("sign-up")
    @Operation(summary = "회원 가입")
    public SignUpResponse signUp(@RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }


    @PostMapping("sign-in")
    @Operation(summary = "로그인")
    public SignInResponse signIn(@RequestBody SignInRequest request) {
        return authService.signIn(request);
    }

    @PostMapping("/reissue-access-token")
    @Operation(summary = "Access Token 재발급")
    public String reissueAccessToken(@RequestHeader("Refresh-Token") String refreshToken, HttpServletRequest request) throws Exception {
        // authService를 통해 tokenProvider에 접근하여 access token 재발급
        String newAccessToken = authService.reissueAccessToken(refreshToken, request);

        // 재발급된 access token 반환
        return newAccessToken;
    }
}