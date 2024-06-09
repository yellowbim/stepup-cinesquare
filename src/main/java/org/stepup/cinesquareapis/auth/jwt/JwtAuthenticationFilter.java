package org.stepup.cinesquareapis.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
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
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;
import org.stepup.cinesquareapis.util.CookieUtil;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 엑세스 토큰 추출
            String accessToken = extractAccessToken(request);

            // 2. 로그인 정보 객체(spring security 지원 user) or 익명 객체 반환
            User user = getUserFromAccessToken(accessToken);

            // 3. 스프링 시큐리티에서 사용할 UsernamePasswordAuthenticationToken 객체를 생성
            AbstractAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(user, accessToken, user.getAuthorities());
            authenticated.setDetails(new WebAuthenticationDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticated);
        }
        catch (ExpiredJwtException e) {
            // 4. Jwt 토큰이 만료됨, Refresh Token이 유효하다면 Access Token 재생성
            try {
                String refreshToken = extractRefreshToken(request);
                handleExpiredToken(request, response, refreshToken);
                return;
            } catch (RestApiException re) {
                request.setAttribute("exception", re);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, re.getErrorCode().getMessage());
                return;
            }
        } catch (RestApiException e) {
            request.setAttribute("exception", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getErrorCode().getMessage());
            return;
        } catch (Exception e) {
            request.setAttribute("exception", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // 엑세스 토큰 추출 메서드
    private String extractAccessToken(HttpServletRequest request) {
        return parseBearerToken(request, HttpHeaders.AUTHORIZATION);
    }

    // Bearer 토큰 파싱 메서드
    // HTTP 요청의 헤더에서 headerName(Authorization) 으로 값을 찾아서
    // Bearer로 시작하는지 확인 후
    // 접두어를 제외한 토큰값으로 파싱
    // 그 외에는 null을 반환
    private String parseBearerToken(HttpServletRequest request, String headerName) {
        return Optional.ofNullable(request.getHeader(headerName))
                .filter(token -> token.length() > 7 && token.substring(0, 7).equalsIgnoreCase("Bearer "))
                .map(token -> token.substring(7))
                .orElse(null);
    }

    // 로그인 정보 객체 반환 메서드
    // 파싱된 토큰이 null이 아니면서 길이가 너무 짧지 않을 때
    // 토큰을 복호화하여
    // userId와 RoleType을 토대로 스프링 시큐리티에서 사용하는 User 객체를 반환
    // 그 외에는 익명 객체를 생성
    private User getUserFromAccessToken(String token) {
        String[] split = Optional.ofNullable(token)
                .filter(subject -> subject.length() >= 10)
                .map(tokenProvider::validateTokenAndGetSubject)
                .orElse("anonymous:anonymous")
                .split(":");

        return new User(split[0], "", List.of(new SimpleGrantedAuthority(split[1])));
    }

    // 사용자 인증 처리 메서드
    private void authenticateUser(HttpServletRequest request, String accessToken, User user) {
        AbstractAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(user, accessToken, user.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    // Refresh Token을 추출하는 메서드
    private String extractRefreshToken(HttpServletRequest request) {
        String refreshToken = CookieUtil.getCookieValue(request, "Refresh-Token");
        if (refreshToken == null) {
            throw new RestApiException(CustomErrorCode.EXPIRED_ACCESS_TOKEN);
        }

        return refreshToken;
    }

    // Access Token이 만료됐을 때의 프로세스
    private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) throws IOException {
        try {
            // 토큰 검증
            String oldAccessToken = extractAccessToken(request);
            tokenProvider.validateRefreshToken(refreshToken, oldAccessToken);

            // 새 Access Token 생성
            String newAccessToken = tokenProvider.recreateAccessToken(oldAccessToken);
            User user = getUserFromAccessToken(newAccessToken);
            authenticateUser(request, newAccessToken, user);

            response.setHeader("New-Access-Token", newAccessToken);

            // 새로운 Refresh Token 발급 및 쿠키 설정
            String newRefreshToken = tokenProvider.createRefreshToken();
            CookieUtil.addCookie(response, "Refresh-Token", newRefreshToken, 7 * 24 * 60 * 60); // 쿠키의 유효기간을 7일로 설정
        } catch (RestApiException ex) {
            request.setAttribute("exception", ex);
            response.sendError(ex.getErrorCode().getHttpStatus().value(), ex.getErrorCode().getMessage());
        } catch (Exception ex) {
            request.setAttribute("exception", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired and reissue failed");
        }
    }

    public String reissueAccessToken(String refreshToken, HttpServletRequest request) throws Exception {
        try {
            String oldAccessToken = extractAccessToken(request);
            tokenProvider.validateRefreshToken(refreshToken, oldAccessToken);

            String newAccessToken = tokenProvider.recreateAccessToken(oldAccessToken);
            User user = getUserFromAccessToken(newAccessToken);
            authenticateUser(request, newAccessToken, user);

            return newAccessToken;
        } catch (Exception e) {
            throw new Exception("Failed to reissue access token", e);
        }
    }
}