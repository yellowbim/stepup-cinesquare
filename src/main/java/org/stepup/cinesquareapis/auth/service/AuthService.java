package org.stepup.cinesquareapis.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.auth.entity.UserRefreshToken;
import org.stepup.cinesquareapis.auth.jwt.JwtAuthenticationFilter;
import org.stepup.cinesquareapis.auth.jwt.TokenProvider;
import org.stepup.cinesquareapis.auth.dto.SignInRequest;
import org.stepup.cinesquareapis.auth.dto.SignInResponse;
import org.stepup.cinesquareapis.auth.dto.SignUpRequest;
import org.stepup.cinesquareapis.auth.dto.SignUpResponse;
import org.stepup.cinesquareapis.auth.repository.UserRefreshTokenRepository;
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

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        User user = userRepository.save(User.from(request, encoder)); // 회원가입시 비밀번호 암호화
        try {
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        return SignUpResponse.from(user);
    }

//    @Transactional(readOnly =
    @Transactional
    public SignInResponse signIn(SignInRequest request) {
            User user = userRepository.findByAccount(request.account())
//                .filter(it -> it.getPassword().equals(request.password()))
                .filter(it -> encoder.matches(request.password(), it.getPassword())) // 암호화된 비밀번호와 비교
                .orElseThrow(() -> new IllegalArgumentException("Invalid account or password."));

        String accessToken = tokenProvider.createAccessToken(String.format("%s:%s", user.getUserId(), user.getType()));	// 토큰 생성
        String refreshToken = tokenProvider.createRefreshToken();

        userRefreshTokenRepository.findById(user.getUserId())
                .ifPresentOrElse(
                        it -> it.updateRefreshToken(refreshToken),
                        () -> userRefreshTokenRepository.save(new UserRefreshToken(user, refreshToken))
                );

        // User 정보 + token 정보
        return new SignInResponse(user.getName(), user.getType(), accessToken, refreshToken);
    }

    @Transactional
    public String reissueAccessToken(String refreshToken, HttpServletRequest request) throws Exception {
        if (refreshToken == null) {
            throw new Exception();
        }
        return jwtAuthenticationFilter.reissueAccessToken(refreshToken, request);
    }
}
