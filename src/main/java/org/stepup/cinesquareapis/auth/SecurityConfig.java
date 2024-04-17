package org.stepup.cinesquareapis.auth;

//public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http
////            .cors(cors -> corsConfigurationSource())
//            .csrf(csrf -> csrf.disable())
////            .exceptionHandling(req -> req.authenticationEntryPoint(jwtAuthEntryPoint))
//            .authorizeHttpRequests(authorizeRequests ->
//                    authorizeRequests
//                            .requestMatchers(HttpMethod.POST, "/api/v1/user/login").permitAll()
//                            .requestMatchers("/v3/**", "/swagger-ui/**").permitAll()
//                            .requestMatchers(CorsUtils::isPreFlightRequest)
//                            .permitAll()
//                            .anyRequest()
//                            .authenticated()
//            )
////            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//    return http.build();
//}

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
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(allowedUrls).permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않으므로 STATELESS 설정
            )
            .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
        return http.build();
    }

    // 스프링 시큐리티를 통해 암호화를 진행하려면
    // 스프링 시큐리티 설정에서 PasswordEncoder를 구현한 클래스를 빈으로 추가해야 함
    // BCryptPasswordEncoder: 스프링 시큐리티에서 기본으로 제공하는 암호화 모듈
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}