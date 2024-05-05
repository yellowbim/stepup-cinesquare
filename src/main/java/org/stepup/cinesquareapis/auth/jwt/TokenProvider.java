package org.stepup.cinesquareapis.auth.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.auth.repository.UserRefreshTokenRepository;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

// JWT 생성 및 복호화
@PropertySource("classpath:jwt.yml")
@Service
public class TokenProvider {
    private final String secretKey;
    private final String expirationHours;
    private final String refreshExpirationHours;
    private final String issuer;

//    private final long reissueLimit;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public TokenProvider(
            @Value("${secret-key}") String secretKey,
            @Value("${expiration-hours}") String expirationHours,
            @Value("${refresh-expiration-hours}") String refreshExpirationHours,
            @Value("${issuer}") String issuer,
            UserRefreshTokenRepository userRefreshTokenRepository
    ) {
        this.secretKey = secretKey;
        this.expirationHours = expirationHours;
        this.refreshExpirationHours = refreshExpirationHours;
        this.issuer = issuer;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
//        reissueLimit = refreshExpirationHours / expirationHours;	// 재발급 한도
    }

    // access token 생성
    public String createAccessToken(String userSpecification) {
        return Jwts.builder()
                .signWith(new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName()))   // HS512 알고리즘을 사용하여 secretKey를 이용해 서명
                .setSubject(userSpecification)  // JWT 토큰 제목
                .setIssuer(issuer)  // JWT 토큰 발급자
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))    // JWT 토큰 발급 시간
                .setExpiration(Date.from(Instant.now().plus(Long.parseLong(expirationHours), ChronoUnit.HOURS)))    // JWT 토큰 만료 시간
                .compact(); // JWT 토큰 생성
    }

    // refresh token 생성
    // 리프레시 토큰은 사용자와 관련된 정보를 전혀 담지 않을 것이기 때문에 subject는 따로 설정하지 않음
    // 발급자와 발급시간, 만료시간만 설정
    public String createRefreshToken() {
        return Jwts.builder()
                .signWith(new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName()))
                .setIssuer(issuer)
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .setExpiration(Date.from(Instant.now().plus(Long.parseLong(expirationHours), ChronoUnit.HOURS)))
                .compact();
    }

    // Subject에는 SignService의 singIn()에서 토큰을 생성할 때 인자로 넘긴 "{회원ID}:{회원타입}" 이 있음
    public String validateTokenAndGetSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Transactional
    public String recreateAccessToken(String oldAccessToken) throws JsonProcessingException {
        String subject = decodeJwtPayloadSubject(oldAccessToken);

        Integer userId = Integer.parseInt(decodeJwtPayloadSubject(oldAccessToken).split(":")[0]);

        userRefreshTokenRepository.findByUserIdAndReissueCountLessThan(userId, 10)
                .ifPresentOrElse(
//                        UserRefreshToken::increaseReissueCount,
                        userRefreshToken -> {
                            userRefreshToken.increaseReissueCount();
                            userRefreshTokenRepository.save(userRefreshToken);
                        },
                        () -> { throw new ExpiredJwtException(null, null, "Refresh token expired."); }
                );

        return createAccessToken(subject);
    }

    @Transactional(readOnly = true)
    public void validateRefreshToken(String refreshToken, String oldAccessToken) throws JsonProcessingException {
        validateAndParseToken(refreshToken);

        Integer userId = Integer.parseInt(decodeJwtPayloadSubject(oldAccessToken).split(":")[0]);

        userRefreshTokenRepository.findByUserIdAndReissueCountLessThan(userId, 10)
                .filter(userRefreshToken -> userRefreshToken.validateRefreshToken(refreshToken))
                .orElseThrow(() -> new ExpiredJwtException(null, null, "Refresh token expired."));
    }

    // validateTokenAndGetSubject에서 따로 분리
    private Jws<Claims> validateAndParseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token);
    }

    private String decodeJwtPayloadSubject(String oldAccessToken) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(
                new String(Base64.getDecoder().decode(oldAccessToken.split("\\.")[1]), StandardCharsets.UTF_8),
                Map.class
        ).get("sub").toString();
    }
}