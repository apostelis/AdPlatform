package com.example.adplatform.infrastructure.web.config;

import com.example.adplatform.infrastructure.web.filter.RateLimitingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitingConfig {

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistration() {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RateLimitingFilter());
        registration.addUrlPatterns("/api/*");
        registration.setName("rateLimitingFilter");
        registration.setOrder(1);
        return registration;
    }
}
