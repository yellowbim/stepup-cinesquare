package org.stepup.cinesquareapis.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.auth.jwt.TokenProvider;
import org.stepup.cinesquareapis.auth.model.SignInRequest;
import org.stepup.cinesquareapis.auth.model.SignInResponse;
import org.stepup.cinesquareapis.auth.model.SignUpRequest;
import org.stepup.cinesquareapis.auth.model.SignUpResponse;
import org.stepup.cinesquareapis.user.entity.User;
import org.stepup.cinesquareapis.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
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

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest request) {
            User user = userRepository.findByAccount(request.account())
//                .filter(it -> it.getPassword().equals(request.password()))
                .filter(it -> encoder.matches(request.password(), it.getPassword())) // 암호화된 비밀번호와 비교
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        String token = tokenProvider.createToken(String.format("%s:%s", user.getUserId(), user.getType()));	// 토큰 생성

        return new SignInResponse(user.getName(), user.getType(), token);	// 생성자에 토큰 추가
    }
}
