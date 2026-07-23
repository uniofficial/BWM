# 📖 프로젝트 소개

BWM은 Spring Boot 기반의 실시간 경매 플랫폼입니다.

- JWT 기반 사용자 인증
- 실시간 입찰
- 빠른 입찰(최소 입찰 금액 자동 계산)
- 경매 자동 종료(Scheduler)
- Wallet 기반 포인트 거래
- 관리자 권한 관리
- Swagger API 문서 제공

REST API 중심으로 설계되었으며,
동시성 문제를 고려한 안전한 입찰 처리와 확장 가능한 아키텍처를 목표로 개발하였습니다.

---

# 👥 Team

| 이름 | 역할 | 담당 |
|------|------|------|
| 정윤희 (팀장) | Backend | [Bid] / [Auction] api, Swagger |
| 이성집 | Backend | [Item] api |
| 이헌진 | Backend | [Wallet] / [Admin] api |
| 최정현 | Backend | 인증 / JWT / Security |

---

# 🛠 Tech Stack

### Backend

- Java 21
- Spring Boot 4.1
- Spring Security
- Spring Data JPA
- JWT
- Hibernate
- Validation
- Swagger (OpenAPI)

### Database

- MySQL
- Redis

### Build

- Gradle

---

# 💻 Development Environment

| 항목 | 버전 |
|------|------|
| Java | 21 |
| Spring Boot | 4.1.x |
| Gradle | 8.x |
| MySQL | 8.x |
| Redis | Latest |
| IDE | STS4 / IntelliJ IDEA |
| OS | Windows / macOS |

---

# 📂 Project Structure

```text
src
 ├── auction
 ├── auth
 ├── bid
 ├── item
 ├── wallet
 ├── user
 ├── global
 │    ├── config
 │    ├── exception
 │    ├── response
 │    └── security
 └── BwmApplication
```

---

# 주요 기능

핵심 도메인 흐름:
회원가입 → 지갑 자동 생성 → 포인트 충전 요청 → 관리자 승인 → 포인트 충전
→ 상품 등록(판매자) → 입찰(구매자) → 포인트 차감/환불 → 경매 종료 → 판매 대금 정산

상품 상태 전이:
OPEN → (낙찰자 있음) → SOLD
     → (낙찰자 없음) → UNSOLD
     → (판매자 직접 취소, 입찰 전) → CANCELLED


## 회원

- 회원가입
- 로그인
- 로그아웃
- 회원 탈퇴
- JWT 인증
- Access Token 재발급

---

## 상품

- 상품 등록
- 상품 수정
- 상품 삭제
- 상품 조회
- 이미지 등록

---

## 입찰

- 일반 입찰
- 빠른 입찰
- 최소 입찰 금액 검증
- 최고 입찰자 갱신
- 입찰 내역 조회

---

## 경매

- 경매 생성
- 수동 종료
- 자동 종료
- 낙찰 조회
- 판매 완료 조회

---

## Wallet

- 포인트 충전
- 포인트 차감
- 판매 금액 지급
- 거래 내역 관리

---

# 🔐 인증 방식

- JWT Access Token
- JWT Refresh Token
- Redis 기반 Refresh Token 관리
- Spring Security Filter 기반 인증

---

# ⚙️ 최초 개발 환경 설정

## 1. Clone

```bash
git clone https://github.com/uniofficial/BWM.git
```

---

## 2. Backend

```bash
./gradlew bootRun
```

---

## 3. Frontend

```bash
cd frontend

npm install

npm run dev
```

---

## 4. db

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/minipro2?sessionVariables=FOREIGN_KEY_CHECKS=0
spring.datasource.username=ureca
spring.datasource.password=ureca

---

# 📄 API Documentation

Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

# 🌿 Git Branch Strategy

```
main
  ↑
develop
  ↑
feature/*
```

- main : 운영 브랜치
- develop : 개발 브랜치
- feature/* : 기능 개발

모든 기능은 Pull Request를 통해 develop으로 병합합니다.
