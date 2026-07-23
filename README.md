# BWM (Bid Wallet Market)

> **Spring Boot 기반 실시간 경매 서비스**

BWM은 사용자가 상품을 등록하고 실시간으로 입찰할 수 있는 경매 서비스입니다.  
JWT 기반 인증, 포인트 시스템, 경매 자동 종료, 빠른 입찰, Anti-Sniping 기능 등을 제공합니다.

---

# 👥 Team

| 이름 | 담당 |
|------|------|
| 정윤희 (팀장) | [백엔드] Auction, Bid, Scheduler |
| 이성집 | [백엔드] Item |
| 이헌진 | [백엔드] Wallet, Admin |
| 최정현 | [백엔드] Authentication, Security |

---

# 🛠 Tech Stack

### Backend

- Java 21
- Spring Boot 4.1
- Spring Security
- Spring Data JPA
- JWT
- MySQL
- Gradle

### Frontend

- React
- TypeScript
- Vite

### Documentation

- Swagger (OpenAPI 3)

---

# 📂 Project Structure

```
src
├── auction
├── auth
├── bid
├── item
├── user
├── wallet
├── global
│   ├── config
│   ├── exception
│   ├── response
│   └── security
└── scheduler
```

---

# 🚀 최초 개발 환경 설정

## 1. 저장소 복제

```bash
git clone https://github.com/uniofficial/BWM.git
cd BWM
```

---

## 2. develop 브랜치 최신화

```bash
git switch develop
git fetch origin
git reset --hard origin/develop
```

---

## 3. 작업 브랜치 생성

```bash
git switch -c feat/{issue-number}-{feature-name}
```

예시

```bash
git switch -c feat/120-login
```

---

# ⚙ Backend 실행

## 1. 환경설정 파일 생성

예시 파일을 복사합니다.

### macOS / Linux

```bash
cp src/main/resources/application-local.example.properties \
src/main/resources/application-local.properties
```

### Windows

```cmd
copy src\main\resources\application-local.example.properties ^
src\main\resources\application-local.properties
```

---

## 2. 환경설정 

`application-local.properties`

```
spring.application.name=BWM
auction.scheduler.fixed-delay=60000
auction.scheduler.initial-delay=10000
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.doc-expansion=none
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/minipro2?sessionVariables=FOREIGN_KEY_CHECKS=0
spring.datasource.username=ureca
spring.datasource.password=ureca
#server.servlet.session.persistent=false
spring.jpa.open-in-view=false
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
logging.level.org.springframework.security=DEBUG
file.upload-dir=uploads/items
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
jwt.secret=eytrewttgyregrerehbgferjgbhfkdsbgkfdbsgyretyhtruhterw
jwt.access-expiration=3600000
jwt.refresh-expiration=604800000
```
> `application-local.properties` 파일은 Git에 포함되지 않습니다.

---

## 3. 애플리케이션 실행

### macOS / Linux

```bash
./gradlew bootRun
```

### Windows

```cmd
gradlew.bat bootRun
```

애플리케이션 실행 후

```
http://localhost:8080
```

에서 확인할 수 있습니다.

---

# 💻 Frontend 실행

frontend 디렉터리로 이동

```bash
cd frontend
```

패키지 설치

```bash
npm install
```

`.env.development` 생성

```env
VITE_API_BASE_URL=http://localhost:8080
echo VITE_APP_ENV=development
```

실행

```bash
npm run dev
```

프론트 실행 주소

```
http://localhost:5173
```

---

# 📖 API 문서

애플리케이션 실행 후

```
http://localhost:8080/swagger-ui/index.html
```

에서 Swagger 문서를 확인할 수 있습니다.

---

# 🧪 테스트

Backend 테스트 실행

```bash
./gradlew test
```

---

# 🌱 브랜치 전략

GitHub Flow를 기반으로 개발합니다.

```
develop
│
├── feat/1-login
├── feat/27-wallet
├── feat/48-item
├── feat/71-auction
└── fix/12-auth
```

### 브랜치 규칙

- `develop` 브랜치에서는 직접 작업하지 않습니다.
- 모든 기능은 `feat/*` 브랜치에서 개발합니다.
- 버그 수정은 `fix/*` 브랜치에서 진행합니다.
- 작업 완료 후 Pull Request를 생성합니다.
- PR 승인 후 `develop` 브랜치에 병합합니다.

---

# 💬 Commit Convention

| 타입 | 설명 |
|------|------|
| feat | 새로운 기능 |
| fix | 버그 수정 |
| refactor | 리팩토링 |
| docs | 문서 수정 |
| style | 코드 스타일 수정 |
| test | 테스트 코드 |
| chore | 빌드 및 설정 |

---

# ✨ 주요 기능

### 인증

- 회원가입
- 로그인
- JWT 인증
- Access Token / Refresh Token
- 로그아웃

### 상품

- 상품 등록
- 상품 수정
- 상품 삭제
- 상품 이미지 등록

### 경매

- 입찰
- 빠른 입찰
- 최소 입찰 단위 검증
- Anti-Sniping(자동 시간 연장)
- 경매 수동 종료
- 자동 종료 Scheduler
- 낙찰 조회
- 판매 완료 조회

### Wallet

- 포인트 충전
- 포인트 출금
- 포인트 내역 조회

### 관리자

- 포인트 충전 승인
- 사용자 관리

---

# 📸 Screen Shots

> 프로젝트 완료 후 화면 추가 예정 

- 메인 페이지
- 상품 목록
- 상품 상세
- 입찰 화면
- 마이페이지

---

# 📄 License

This project is licensed under the MIT License.
