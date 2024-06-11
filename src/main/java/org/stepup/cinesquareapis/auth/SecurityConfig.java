package org.stepup.cinesquareapis.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.stepup.cinesquareapis.auth.jwt.JwtAuthenticationFilter;

// 스프링 시큐리티 기본 설정
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 메소드 시큐리니 활성화
public class SecurityConfig {

    // 공개 API 경로
    private final String[] allowedUrls = {"/", "/swagger-ui/**", "/v3/**"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(allowedUrls).permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://cinesquare-s3.s3-website.ap-northeast-2.amazonaws.com");
        configuration.addAllowedOrigin("http://cine-square.s3-website.ap-northeast-2.amazonaws.com");
        configuration.addAllowedOrigin("https://cinesquares3.s3.ap-northeast-2.amazonaws.com");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
