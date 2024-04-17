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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.stepup.cinesquareapis.auth.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 메소드 시큐리니 활성화
public class SecurityConfig {

    //공개 API 경로
    private final String[] allowedUrls = {"/", "/swagger-ui/**", "/v3/**", "/api/auth/**"};
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(allowedUrls).permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger 관련 공개 url
//                        .requestMatchers("/api/auth/**").permitAll()  // 공개 API 경로
                        .anyRequest().authenticated()  // 기타 모든 요청은 인증 필요
                )
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .cors(Customizer.withDefaults()) // CORS 설정 (필요한 경우)
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
        return http.build();
    }

    // 비밀번호 암호화 저장을 위해
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}