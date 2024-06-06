package org.stepup.cinesquareapis.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.auth.dto.SignInRequest;
import org.stepup.cinesquareapis.auth.dto.SignInResponse;
import org.stepup.cinesquareapis.auth.dto.SignUpRequest;
import org.stepup.cinesquareapis.auth.dto.SignUpResponse;
import org.stepup.cinesquareapis.auth.entity.UserRefreshToken;
import org.stepup.cinesquareapis.auth.jwt.JwtAuthenticationFilter;
import org.stepup.cinesquareapis.auth.jwt.TokenProvider;
import org.stepup.cinesquareapis.auth.repository.UserRefreshTokenRepository;
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;
import org.stepup.cinesquareapis.user.entity.User;
import org.stepup.cinesquareapis.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final PasswordEncoder encoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final TokenProvider tokenProvider;

    /**
     * account 존재 여부 확인
     */
    public boolean checkAccount(String account) {
        return userRepository.existsByAccount(account);
    }

    /**
     * 회원가입
     */
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        // 유효성 체크: 존재하는 사용자인지 확인
        userRepository.findByAccount(request.account())
                .orElseThrow(() -> new RestApiException(CustomErrorCode.ALREADY_REGISTED_ACCOUNT));

        // 유저 저장
        User user = userRepository.save(User.from(request, encoder)); // 비밀번호 암호화
        userRepository.save(user);

        return new SignUpResponse(user);
    }

    /**
     * 로그인 Access Token, Refresh Token 생성
     */
    @Transactional
    public SignInResponse signIn(SignInRequest request) {
        // 유효성 체크1: account로 사용자 조회
        User user = userRepository.findByAccount(request.account())
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_USER));

        // 유효성 체크2: 암호화된 비밀번호와 비교
        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new RestApiException(CustomErrorCode.INVALID_PASSWORD);
        }

        // access token, refresh token 생성
        String accessToken = tokenProvider.createAccessToken(String.format("%s:%s", user.getUserId(), user.getType()));
        String refreshToken = tokenProvider.createRefreshToken();

        userRefreshTokenRepository.findById(user.getUserId())
                .ifPresentOrElse(
                        it -> it.updateRefreshToken(refreshToken), // 값이 존재할 때 -> update 업데이트
                        () -> userRefreshTokenRepository.save(new UserRefreshToken(user, refreshToken))
                );

        // User 정보 + token 정보
        return new SignInResponse(user.getName(), user.getType(), accessToken, refreshToken);
    }

    /**
     * Access Token 재발급
     */
    @Transactional
    public String reissueAccessToken(String refreshToken, HttpServletRequest request) throws Exception {
        if (refreshToken == null) {
            throw new Exception();
        }
        return jwtAuthenticationFilter.reissueAccessToken(refreshToken, request);
    }
}
