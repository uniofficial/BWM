package com.bwm.auction.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bwm.auction.service.AuctionService;

import lombok.RequiredArgsConstructor;

/**
 * 마감 시간이 지난 경매를 주기적으로 조회하고
 * 자동 종료 처리를 실행하는 스케줄러 
 */
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(AuctionScheduler.class);

    private final AuctionService auctionService;

    /**
     * 일정 주기마다 만료된 경매를 자동으로 종료함 
     *
     * fixedDelayString:
     * 이전 실행이 완전히 종료된 시점부터
     * 다음 실행까지 대기하는 시간 
     *
     * 기본 실행 간격: 60초
     * 서버 실행 후 최초 대기 시간: 10초
     */
    @Scheduled(
            fixedDelayString = "${auction.scheduler.fixed-delay:60000}",
            initialDelayString = "${auction.scheduler.initial-delay:10000}"
    )
    public void closeExpiredAuctions() {
        log.info("Expired auction scheduler started.");

        // OPEN 상태이면서 마감 시간이 지난 상품 ID 조회 
        List<Integer> expiredAuctionIds =
                auctionService.findExpiredAuctionIds();

        log.info(
                "Expired auction count: {}",
                expiredAuctionIds.size()
        );

        /*
         * 각 상품을 별도의 트랜잭션으로 종료 
         *
         * 하나의 상품 처리 중 예외가 발생해도
         * catch 블록에서 처리하고 다음 상품으로 계속 진행함 
         */
        for (Integer itemId : expiredAuctionIds) {
            try {
                auctionService.closeExpiredAuction(itemId);

                log.info(
                        "Expired auction closed successfully. itemId={}",
                        itemId
                );
            } catch (Exception e) {
                log.error(
                        "Failed to close expired auction. itemId={}",
                        itemId,
                        e
                );
            }
        }

        log.info("Expired auction scheduler finished.");
    }
}