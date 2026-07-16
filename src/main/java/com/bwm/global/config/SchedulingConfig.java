package com.bwm.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Scheduler 활성화하는 설정 클래스 
 *
 * @EnableScheduling이 등록돼야 
 * 프로젝트 내부의 @Scheduled 메서드가 일정 주기마다 실행됨 
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

    /*
     * 별도의 Bean 설정이 필요하지 않으므로
     * 현재는 비어 있는 설정 클래스로 사용
     */
}