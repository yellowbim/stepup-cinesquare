package org.stepup.cinesquareapis.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// JWT를 통해 권한을 부여하는 필터

// @Order를 통해 int범위 내에서 의존성 주입 우선순위를 설정 (수치가 낮을수록 높음)
// 우선순위를 너무 높이면(= 값이 너무 작으면) 유효한 토큰이라도 인증이 안되고,
// 우선순위가 너무 낮으면(= 값이 너무 크면) 토큰이 없어도 통과되기 때문에 적당한 값으로 설정해야함.
@Order(0)
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    // 인증 정보를 설정
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {
        // 1. 엑세스 토큰 값 파싱
        String token = parseBearerToken(request);

        // 2. 로그인 정보 포함 객체 or 익명 객체 반환
        User user = parseUserSpecification(token);

        // 3. 스프링 시큐리티에서 사용할 UsernamePasswordAuthenticationToken 객체를 생성
        AbstractAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(user, token, user.getAuthorities());
        authenticated.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticated);

        filterChain.doFilter(request, response);
    }

    // HTTP 요청의 헤더에서 Authorization값을 찾아서
    // Bearer로 시작하는지 확인 후 접두어를 제외한 토큰값으로 파싱
    // 그 외에는 null을 반환
    private String parseBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(token -> token.substring(0, 7).equalsIgnoreCase("Bearer "))
                .map(token -> token.substring(7))
                .orElse(null);
    }

    // 토큰에 담긴 회원ID와 RoleType을 토대로 스프링 시큐리티에서 사용할 User 객체를 반환
    // 파싱된 토큰이 null이 아니면서 길이가 너무 짧지 않을 때만 토큰을 복호화
    // 그 외에는 익명 객체를 생성
    private User parseUserSpecification(String token) {
        String[] split = Optional.ofNullable(token)
                .filter(subject -> subject.length() >= 10)
                .map(tokenProvider::validateTokenAndGetSubject)
                .orElse("anonymous:anonymous")
                .split(":");

        return new User(split[0], "", List.of(new SimpleGrantedAuthority(split[1])));
    }
}