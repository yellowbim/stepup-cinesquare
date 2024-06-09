package org.stepup.cinesquareapis.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.auth.dto.SignInRequest;
import org.stepup.cinesquareapis.auth.dto.SignInResponse;
import org.stepup.cinesquareapis.auth.dto.SignUpRequest;
import org.stepup.cinesquareapis.auth.dto.SignUpResponse;
import org.stepup.cinesquareapis.auth.entity.UserRefreshToken;
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
        userRepository.findByAccount(request.getAccount())
                .ifPresent(user -> {
                    throw new RestApiException(CustomErrorCode.ALREADY_REGISTED_ACCOUNT);
                });

        // 유저 저장
        User user = userRepository.save(request.toEntity(encoder)); // 비밀번호 암호화
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

        // Access Token, Refresh Token 생성
        String userSpecification = String.format("%s:%s", user.getUserId(), user.getType());
        String accessToken = tokenProvider.createAccessToken(userSpecification);
        String refreshToken = tokenProvider.createRefreshToken();
        int expirationTime = tokenProvider.getRefreshTokenExpirationTime();

        // Refresh Token 조회
        userRefreshTokenRepository.findById(user.getUserId())
                .ifPresentOrElse(
                        // DB에 Refresh Token이 존재하면
                        existingToken -> {
                            // Refresh Token 만료 여부 확인, 만료 되면 업데이트
                            if (existingToken.isExpired()) {
                                existingToken.updateRefreshToken(refreshToken, expirationTime);
                                userRefreshTokenRepository.save(existingToken);
                            }
                        },
                        //  DB에 Refresh Token이 존재하지 않으면, 저장
                        () -> {
                            UserRefreshToken newUserRefreshToken = new UserRefreshToken(user, refreshToken, expirationTime);
                            userRefreshTokenRepository.save(newUserRefreshToken);
                        }
                );

        // User 정보 + token 정보
        return new SignInResponse(user, accessToken, refreshToken);
    }
}
