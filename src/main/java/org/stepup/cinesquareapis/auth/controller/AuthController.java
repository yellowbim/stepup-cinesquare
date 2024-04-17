package org.stepup.cinesquareapis.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stepup.cinesquareapis.auth.service.AuthService;
import org.stepup.cinesquareapis.auth.model.SignInRequest;
import org.stepup.cinesquareapis.auth.model.SignInResponse;
import org.stepup.cinesquareapis.auth.model.SignUpRequest;
import org.stepup.cinesquareapis.auth.model.SignUpResponse;

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
}

