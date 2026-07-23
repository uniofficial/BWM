package com.bwm.wallet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwm.item.entity.Item;
import com.bwm.item.repository.ItemRepository;
import com.bwm.user.entity.User;
import com.bwm.user.repository.UserRepository;
import com.bwm.wallet.dto.WalletResponseDto;
import com.bwm.wallet.dto.WalletChargeRequestResponseDto;
import com.bwm.wallet.entity.Wallet;
import com.bwm.wallet.entity.WalletHistory;
import com.bwm.wallet.entity.WalletHistoryType;
import com.bwm.wallet.entity.WalletChargeRequest;
import com.bwm.wallet.entity.ChargeRequestStatus;
import com.bwm.wallet.exception.WalletNotFoundException;
import com.bwm.wallet.repository.WalletHistoryRepository;
import com.bwm.wallet.repository.WalletRepository;
import com.bwm.wallet.repository.WalletChargeRequestRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {
        private final WalletRepository walletRepository;
        private final WalletHistoryRepository walletHistoryRepository;
        private final ItemRepository itemRepository;
        private final UserRepository userRepository;
        private final WalletChargeRequestRepository walletChargeRequestRepository;

        @Override
        public WalletResponseDto getMyWallet(String userUuid) {
                User user = getUserByUserUuid(userUuid);
                Wallet wallet = walletRepository.findById(user.getUserId())
                                .orElseGet(() -> { // 지갑이 없으면 신규 생성 후 저장
                                        Wallet newWallet = Wallet.builder()
                                                        .user(user)
                                                        .balance(0)
                                                        .build();
                                        return walletRepository.save(newWallet);
                                });
                return new WalletResponseDto(wallet.getBalance());
        }

        private User getUserByUserUuid(String userUuid) {
                if (userUuid == null || userUuid.isBlank()) {
                        throw new IllegalArgumentException("인증된 사용자 식별자가 없습니다.");
                }
                User user = userRepository.findByUserUuid(userUuid)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "사용자를 찾을 수 없습니다. userUuid=" + userUuid));
                // isDeleted check is handled by @SQLRestriction on User entity implicitly,
                // but we can add explicit check if needed.
                return user;
        }

        @Override
        @Transactional
        public WalletChargeRequestResponseDto requestPointCharge(String userEmail, Integer amount) {
                User user = getUserByEmail(userEmail);
                if (amount <= 0) {
                        throw new IllegalArgumentException("충전 요청 금액은 1원 이상이어야 합니다.");
                }

                WalletChargeRequest request = WalletChargeRequest.builder()
                                .user(user)
                                .amount(amount)
                                .status(ChargeRequestStatus.PENDING)
                                .createdAt(LocalDateTime.now())
                                .build();

                WalletChargeRequest saved = walletChargeRequestRepository.save(request);
                return convertToResponseDto(saved);
        }

        @Override
        public List<WalletChargeRequestResponseDto> getMyChargeRequests(String userEmail) {
                getUserByEmail(userEmail); // 유저 존재 및 탈퇴 여부 검증
                List<WalletChargeRequest> requests = walletChargeRequestRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
                return requests.stream()
                                .map(this::convertToResponseDto)
                                .collect(Collectors.toList());
        }

        @Override
        public List<WalletChargeRequestResponseDto> getAllChargeRequests() {
                List<WalletChargeRequest> requests = walletChargeRequestRepository.findAllByOrderByCreatedAtDesc();
                return requests.stream()
                                .map(this::convertToResponseDto)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public void approvePointCharge(Integer requestId) {
                WalletChargeRequest request = walletChargeRequestRepository.findById(requestId)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 충전 요청입니다. ID=" + requestId));

                request.approve();
                chargeUserPoint(request.getUser().getUserId(), request.getAmount());
        }

        @Override
        @Transactional
        public void rejectPointCharge(Integer requestId) {
                WalletChargeRequest request = walletChargeRequestRepository.findById(requestId)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 충전 요청입니다. ID=" + requestId));

                request.reject();
        }

        private WalletChargeRequestResponseDto convertToResponseDto(WalletChargeRequest request) {
                return WalletChargeRequestResponseDto.builder()
                                .chargeRequestId(request.getChargeRequestId())
                                .userId(request.getUser().getUserId())
                                .userEmail(request.getUser().getEmail())
                                .amount(request.getAmount())
                                .status(request.getStatus().name())
                                .createdAt(request.getCreatedAt())
                                .processedAt(request.getProcessedAt())
                                .build();
        }

        private User getUserByEmail(String email) {
                if (email == null || email.isBlank()) {
                        throw new IllegalArgumentException("인증된 사용자 이메일이 없습니다.");
                }
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. email=" + email));
                return user;
        }

        @Override
        @Transactional
        public void chargeUserPoint(Integer userId, Integer amount) {
                Wallet wallet = walletRepository.findById(userId)
                                .orElseThrow(() -> new WalletNotFoundException("해당 유저의 지갑을 찾을 수 없습니다."));

                wallet.charge(amount);

                WalletHistory history = WalletHistory.builder()
                                .wallet(wallet)
                                .item(null)
                                .type(WalletHistoryType.CHARGE)
                                .amount(amount)
                                .balanceAfter(wallet.getBalance())
                                .build();

                walletHistoryRepository.save(history);
        }

        @Override
        @Transactional
        public void deductBidPoint(Integer userId, Integer itemId, Integer amount) {
                Wallet wallet = walletRepository.findById(userId)
                                .orElseThrow(() -> new WalletNotFoundException("해당 유저의 지갑을 찾을 수 없습니다."));

                Item item = itemRepository.findById(itemId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

                wallet.deduct(amount);

                WalletHistory history = WalletHistory.builder()
                                .wallet(wallet)
                                .item(item)
                                .type(WalletHistoryType.BID_PAYMENT)
                                .amount(amount)
                                .balanceAfter(wallet.getBalance())
                                .build();

                walletHistoryRepository.save(history);
        }

        @Override
        @Transactional
        public void refundBidPoint(Integer userId, Integer itemId, Integer amount) {
                Wallet wallet = walletRepository.findById(userId)
                                .orElseThrow(() -> new WalletNotFoundException("해당 유저의 지갑을 찾을 수 없습니다."));

                Item item = itemRepository.findById(itemId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

                wallet.charge(amount);

                WalletHistory history = WalletHistory.builder()
                                .wallet(wallet)
                                .item(item)
                                .type(WalletHistoryType.REFUND)
                                .amount(amount)
                                .balanceAfter(wallet.getBalance())
                                .build();

                walletHistoryRepository.save(history);
        }

        @Override
        @Transactional
        public void depositSalesRevenue(Integer sellerId, Integer itemId, Integer amount) {
                User seller = userRepository.findById(sellerId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

                Wallet wallet = walletRepository.findById(sellerId)
                                .orElseGet(() -> {
                                        Wallet newWallet = Wallet.builder()
                                                        .user(seller)
                                                        .balance(0)
                                                        .build();
                                        return walletRepository.save(newWallet);
                                });

                wallet.charge(amount);

                Item item = itemRepository.findById(itemId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

                WalletHistory history = WalletHistory.builder()
                                .wallet(wallet)
                                .item(item)
                                .type(WalletHistoryType.SALES_REVENUE)
                                .amount(amount)
                                .balanceAfter(wallet.getBalance())
                                .build();

                walletHistoryRepository.save(history);
        }

        @Override
        @Transactional
        public void createWallet(User user) {
                Wallet newWallet = Wallet.builder()
                                .user(user)
                                .balance(0)
                                .build();
                walletRepository.save(newWallet);
        }

        @Override
        @Transactional
        public void deleteWallet(Integer userId) {
                Wallet wallet = walletRepository.findById(userId)
                                .orElseThrow(() -> new WalletNotFoundException("해당 유저의 지갑을 찾을 수 없습니다."));

                if (wallet.getBalance() > 0) {
                        throw new IllegalStateException("지갑에 잔액이 남아있어 탈퇴할 수 없습니다.");
                }

                walletRepository.delete(wallet);
        }
}
