package com.bwm.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import org.junit.jupiter.api.BeforeEach;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import com.bwm.admin.controller.AdminWalletController;
import com.bwm.auction.controller.AuctionController;
import com.bwm.auction.service.AuctionService;
import com.bwm.auth.controller.AuthController;
import com.bwm.auth.service.AuthService;
import com.bwm.bid.controller.BidController;
import com.bwm.bid.controller.MyBidController;
import com.bwm.bid.service.BidService;
import com.bwm.global.config.SecurityConfig;
import com.bwm.global.config.security.CustomAccessDeniedHandler;
import com.bwm.global.config.security.CustomAuthenticationEntryPoint;
import com.bwm.global.config.security.JwtAuthenticationFilter;
import com.bwm.item.controller.ItemController;
import com.bwm.item.controller.ItemImageController;
import com.bwm.item.controller.UserItemController;
import com.bwm.item.service.ItemImageService;
import com.bwm.item.service.ItemService;
import com.bwm.wallet.controller.WalletController;
import com.bwm.wallet.service.WalletHistoryService;
import com.bwm.wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 모든 컨트롤러 단위 테스트가 상속받는 베이스 클래스.
 * 
 * @WebMvcTest에 테스트할 모든 컨트롤러를 등록하여, 하위 클래스에서 하나만 상속받으면 되도록 구성.
 *              Security 관련 Bean 및 서비스 레이어 Bean들을 일괄 모킹하여 중복 제거.
 */
@Import(SecurityConfig.class)
@WebMvcTest(controllers = {
        AdminWalletController.class,
        AuctionController.class,
        AuthController.class,
        BidController.class,
        MyBidController.class,
        ItemController.class,
        ItemImageController.class,
        UserItemController.class,
        WalletController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    // --- Services ---
    @MockitoBean
    protected AuctionService auctionService;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected BidService bidService;

    @MockitoBean
    protected ItemService itemService;

    @MockitoBean
    protected ItemImageService itemImageService;

    @MockitoBean
    protected WalletService walletService;

    @MockitoBean
    protected WalletHistoryService walletHistoryService;

    // --- Security Beans ---
    @MockitoBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    protected CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @MockitoBean
    protected CustomAccessDeniedHandler customAccessDeniedHandler;

    @BeforeEach
    void setUpSecurityFilter() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }
}
