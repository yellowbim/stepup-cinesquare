package org.stepup.cinesquareapis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.stepup.cinesquareapis.common.log.LoggingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://cinesquare-s3.s3-website.ap-northeast-2.amazonaws.com", "http://cine-square.s3-website.ap-northeast-2.amazonaws.com", "https://cinesquares3.s3.ap-northeast-2.amazonaws.com")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE")
                .maxAge(3000);
    }
}
