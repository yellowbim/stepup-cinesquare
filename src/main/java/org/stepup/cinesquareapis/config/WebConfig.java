package org.stepup.cinesquareapis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://cinesquare-s3.s3-website.ap-northeast-2.amazonaws.com")
                .allowedMethods("GET", "POST", "PATCH")
                .maxAge(3000);
    }
}
