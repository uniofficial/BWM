package com.bwm.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 로컬 디스크에 저장된 상품 이미지 파일을, 외부에서 URL로 요청했을 때 응답해줄 수 있게 등록하는 설정 클래스.
 *
 * 스프링은 기본적으로 컨트롤러에 매핑되지 않은 URL로 요청이 오면 처리하지 못한다.
 * 이미지 파일은 컨트롤러가 아니라 디스크에 그냥 저장된 파일이므로,
 * 별도로 "이 URL 패턴은 이 디스크 경로에서 찾아 응답하라"는 규칙을 등록해줘야
 * 저장된 이미지에 실제로 접근이 가능해진다. 그 등록을 담당하는 클래스다.
 */

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    // application.properties의 file.upload-dir 값. ItemImageServiceImpl이 파일을 저장하는 경로와 동일해야 한다.
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
         // "/images/items/"로 시작하는 요청 URL을
        registry.addResourceHandler("/images/items/**")
                // uploadDir 하위의 동일한 상대 경로 파일과 매칭시켜 응답한다.
                .addResourceLocations("file:" + uploadDir + "/"); }
}