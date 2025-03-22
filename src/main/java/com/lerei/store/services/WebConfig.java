package com.lerei.store.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure Spring Boot to serve images from the static files directory
        registry.addResourceHandler("/files/**")
                .addResourceLocations("classpath:/static/files/"); // Use classpath for static content
    }
}
