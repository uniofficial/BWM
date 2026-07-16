package com.bwm.auction.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 마감 시간이 지난 경매를 주기적으로 확인하고
 * 자동 종료 처리를 시작하는 스케줄러 
 **/

 /* TODO
 * 실제 종료 대상 조회와 상태 변경은
 * ItemRepository 및 AuctionService가 준비된 후 연결할 것 
 */
@Component
public class AuctionScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(AuctionScheduler.class);

    /**
     * 일정 주기마다 만료된 경매를 확인함 
     *
     * fixedDelayString: 이전 실행이 끝난 시점부터 다음 실행까지의 대기 시간
     * 기본값은 60,000ms(1분) 
     */
    @Scheduled(
        fixedDelayString = "${auction.scheduler.fixed-delay:60000}",
        initialDelayString = "${auction.scheduler.initial-delay:10000}"
    )
    public void closeExpiredAuctions() {

        log.info("Expired auction scheduler started.");

        /*
         * TODO: ItemRepository 구현 후 다음 흐름 연결
         *
         * 1. 현재 상태가 OPEN인 상품 조회
         * 2. auctionEndAt이 현재 시각 이하인 상품 조회
         * 3. 조회된 각 상품에 대해 경매 종료 서비스 호출
         * 4. highestBidder가 존재하면 SOLD 처리
         * 5. highestBidder가 없으면 UNSOLD 처리
         * 6. 하나의 상품 처리 중 예외가 발생하더라도 나머지 상품 처리는 계속 진행
         */

        log.info("Expired auction scheduler finished.");
    }
}