package com.head.wordeasebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 允许所有的路径
                .allowedOriginPatterns("*")  // 允许的来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许所有的 HTTP 方法（GET, POST, PUT, DELETE 等）
                .allowedHeaders("*")  // 允许所有的请求头
                .allowCredentials(true)  // 允许发送 Cookie 等凭证
                .maxAge(3600);
    }
}
