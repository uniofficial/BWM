package com.bwm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BWM 애플리케이션 실행 클래스
 *
 * @EnableScheduling을 통해
 * 프로젝트 내부의 @Scheduled 메서드를 활성화함 
 */
@EnableScheduling
@SpringBootApplication
public class BwmApplication {

    public static void main(String[] args) {
        SpringApplication.run(BwmApplication.class, args);
    }
}