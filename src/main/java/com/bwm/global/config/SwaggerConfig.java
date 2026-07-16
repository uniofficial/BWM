package com.bwm.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * BWM REST API의 Swagger/OpenAPI 문서 설정
 *
 * API 기본 정보와 JWT Bearer 인증 방식을 Swagger UI에 등록함 
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";
    
    @Bean
    public OpenAPI bwmOpenAPI() {

        Info info = new Info()
                .title("BWM API")
                .description("포인트 기반 경매 마켓 BWM REST API 명세")
                .version("v1.0.0");

        SecurityScheme securityScheme = new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        Components components = new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme);

        SecurityRequirement securityRequirement =
                new SecurityRequirement().addList(SECURITY_SCHEME_NAME);

        return new OpenAPI()
                .info(info)
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}