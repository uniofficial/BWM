# BWM Frontend 운영 체크리스트

이 문서는 배포 담당자와 프론트·백엔드 개발자가 같은 기준으로 배포 상태를 확인하기 위한 체크리스트입니다. 실제 Production 인프라에 접속하거나 배포를 수행하는 문서가 아닙니다.

## 1. 현재 운영 준비 상태

| 영역 | 현재 상태 | 담당 |
|---|---|---|
| Production API Origin | 미확정 | 인프라·백엔드 |
| 정적 호스팅 플랫폼 | 미확정 | 인프라 |
| SPA fallback | 가이드만 작성 | 인프라·프론트 |
| CORS | 로컬 Origin만 허용 | 백엔드 |
| Refresh Cookie | 개발용 `Secure=false`, `SameSite=Strict` | 백엔드 |
| Request ID | 프론트 수집 준비 완료, 서버 발급 없음 | 백엔드 |
| 오류 분류 | 프론트 공통 `ApiError`에 적용 | 프론트 |
| 외부 APM·분석 SDK | 사용하지 않음 | 정책 |
| 로그아웃·회원 탈퇴 | 프론트·백엔드 연동 완료 | 프론트·백엔드 |
| 상품 상세 갱신 | 5초 Polling, 중첩 방지 적용 | 프론트 |

## 2. 배포 전

### 소스와 Build

- [ ] 배포 대상 Branch와 Commit SHA를 기록했다.
- [ ] Node.js가 `^20.19.0 || >=22.12.0` 범위다.
- [ ] `npm ci`가 성공한다.
- [ ] `npm run lint`가 성공한다.
- [ ] 실제 HTTPS API Origin으로 Production Build가 성공한다.
- [ ] `dist/`에 Source Map이 생성되지 않았다.
- [ ] `VITE_` 환경 변수에 Token, 비밀번호, Secret이 없다.
- [ ] `VITE_APP_VERSION`, `VITE_COMMIT_SHA`를 Build 시점에 주입했다.

```bash
VITE_API_BASE_URL=https://api.example.com \
VITE_APP_ENV=production \
VITE_APP_VERSION=<release-version> \
VITE_COMMIT_SHA=<commit-sha> \
npm run build -- --mode production
```

### 정적 호스팅

- [ ] `/products`, `/products/{id}`, `/login`, `/mypage/wallet` 직접 접근이 `/index.html`로 fallback 된다.
- [ ] `/assets/*` 요청은 SPA fallback 대상이 아니다.
- [ ] `index.html`은 `no-cache`다.
- [ ] 해시 Asset은 `public, max-age=31536000, immutable`이다.
- [ ] 이전 정상 Artifact 또는 Commit으로 되돌리는 절차가 준비됐다.

### API·인증·Cookie

- [ ] `VITE_API_BASE_URL`에 `/api`, Query, Fragment가 없다.
- [ ] 프론트 Origin이 백엔드 CORS allowlist에 정확히 등록됐다.
- [ ] `Allow-Credentials: true`이며 허용 Origin이 `*`가 아니다.
- [ ] `Accept`, `Authorization`, `Content-Type`과 필요한 HTTP Method가 허용된다.
- [ ] Refresh Cookie가 Production에서 `HttpOnly`, `Secure`다.
- [ ] 실제 도메인 관계에 맞춰 `SameSite`, `Domain`, `Path`를 검증했다.
- [ ] Access Token 응답 Header `Authorization`이 브라우저에 노출된다.
- [ ] Access Token 만료 시 Refresh 요청이 한 번만 발생하고 원 요청도 한 번만 재시도된다.
- [ ] Refresh 실패 후 보호 페이지가 로그인 상태로 남지 않는다.

### 이미지 업로드

- [ ] 브라우저가 Multipart boundary를 자동 생성한다.
- [ ] Proxy와 백엔드의 요청 크기 제한이 프론트 제한 이상이다.
- [ ] JPG, PNG, WebP 업로드를 확인했다.
- [ ] 파일당 10MB, 전체 50MB 초과 시 사용자 메시지를 확인했다.
- [ ] 이미지 Storage와 반환 URL이 HTTPS다.

## 3. 배포 직후 Smoke Test

운영 데이터 변경이 필요한 항목은 사전에 지정한 테스트 계정과 테스트 상품에서만 실행합니다. 입찰·충전·경매 종료를 임의로 반복하지 않습니다.

| 순서 | 확인 항목 | 성공 기준 | 데이터 변경 |
|---:|---|---|---:|
| 1 | `/` 및 `/products` | 화면 표시, Console 치명 오류 없음 | 아니요 |
| 2 | `/products` 직접 접근·새로고침 | 정적 404 없이 목록 표시 | 아니요 |
| 3 | 유효한 `/products/{id}` | 상세·이미지·가격 표시 | 아니요 |
| 4 | 존재하지 않는 프론트 Route | 앱의 Not Found 표시 | 아니요 |
| 5 | 로그인 | Access Token Header 수신, 사용자 상태 반영 | 세션 |
| 6 | 보호 Route 복귀 | 로그인 후 원래 Route로 복귀 | 세션 |
| 7 | 상품 상세 Polling | 5초 주기, 요청 중첩 없음, 화면 멈춤 없음 | 아니요 |
| 8 | 상품 등록·이미지 | 지정된 테스트 상품 1건만 생성 | 예 |
| 9 | 입찰 | 지정된 경매에 승인된 테스트 금액 1회 | 예 |
| 10 | 지갑·내역 조회 | 잔액과 내역 응답 정상 | 아니요 |
| 11 | 충전 요청 | 지정된 최소 테스트 금액 1회 | 예 |
| 12 | 경매 종료 | 종료 전용 테스트 상품 1건 | 예 |
| 13 | Token 만료·Refresh | 동시 401에도 Refresh 단일 실행 | 세션 |
| 14 | 로그아웃·회원 탈퇴 | 로그아웃 시 Refresh Cookie 즉시 만료, 탈퇴 제한 사유 메시지 표시 | 예 |

변경 요청이 실패하거나 응답이 불명확하면 같은 요청을 즉시 반복하지 않습니다. Network의 Method, Endpoint, Status, Request ID와 서버 반영 여부를 먼저 확인합니다.

## 4. 브라우저 진단

### Console

- `[BWM operational error]` 레코드의 `category`, `status`, `code`, `endpoint`, `requestId`를 확인합니다.
- `appVersion`, `commitSha`로 현재 Bundle을 확인합니다.
- 오류 레코드에 Token, Cookie, 요청·응답 본문, 사용자 전체 객체가 포함되면 즉시 공유를 중단하고 프론트 담당자에게 알립니다.

### Network

- 요청 URL이 예상 API Origin인지 확인합니다.
- 404는 프론트 Route, 정적 Asset, API Endpoint 중 어디서 발생했는지 구분합니다.
- 응답이 없는 경우 Console의 CORS·Mixed Content·인증서 오류를 함께 확인합니다.
- 401 연속 발생 시 `/api/auth/reissue` 횟수와 원 요청 재시도 횟수를 확인합니다.
- Multipart 요청의 `Content-Type` boundary를 수동으로 덮어쓰지 않았는지 확인합니다.

### Cookie

- HttpOnly Refresh Cookie 값은 복사하거나 공유하지 않습니다.
- Cookie 존재 여부, Domain, Path, Secure, SameSite, 만료 시각만 확인합니다.
- 프론트와 API가 다른 Site라면 `SameSite=None; Secure`와 정확한 CORS Origin이 함께 필요합니다.

## 5. 변경 요청 재시도 원칙

| 요청 유형 | 자동 재시도 | 수동 재시도 조건 |
|---|---:|---|
| GET·HEAD 조회 | 제한적으로 가능 | 화면의 명시적 다시 시도 또는 다음 Polling |
| 로그인·회원가입 | 안 함 | 서버 반영 여부 확인 후 사용자 직접 실행 |
| 상품 등록·이미지 업로드 | 안 함 | 상품 생성 여부와 업로드 결과 확인 후 판단 |
| 입찰 | 안 함 | 입찰 내역·현재가 확인 후 판단 |
| 충전 요청 | 안 함 | 충전 요청 내역 확인 후 판단 |
| 경매 종료 | 안 함 | 상품 상태 확인 후 판단 |
| 로그아웃·회원 탈퇴 | 안 함 | 실패 시 세션·계정 상태 확인 후 판단 |

프론트 Axios 인증 Interceptor의 401 재시도는 Access Token 갱신 후 원 요청을 최대 한 번만 재전송합니다. 이는 일반 장애 재시도와 구분합니다.

## 6. 배포 후 관찰

외부 모니터링 도구는 도입하지 않았습니다. 운영 도구가 정해지기 전까지 아래 항목을 배포 담당자가 수동 확인합니다.

- [ ] 첫 화면과 주요 Route의 정적 Asset 404 여부
- [ ] API 401, 403, 409, 5xx 비율의 이상 증가 여부
- [ ] Login과 Refresh 성공 여부
- [ ] 상품 상세 Polling이 서버 부하를 유발하지 않는지
- [ ] 입찰·충전·경매 종료의 중복 처리 신고 여부
- [ ] 이미지 업로드의 413, 415, Timeout 신고 여부
- [ ] 새 배포 Commit에서만 재현되는 Runtime 오류 여부

자동화 후보는 별도 합의 후 도입합니다: 프론트 합성 모니터링, API Health Check, 오류 수집, Request ID 검색, 핵심 전환·오류율 대시보드.

## 7. 배포 종료 조건

- [ ] 모든 읽기 Smoke Test가 통과했다.
- [ ] 승인된 변경 Smoke Test가 중복 없이 통과했다.
- [ ] 치명적인 Console·Network 오류가 없다.
- [ ] 실패 항목은 담당자, 심각도, 임시 조치, 다음 확인 시각과 함께 기록했다.
- [ ] 배포 Commit과 확인자가 남아 있다.
- [ ] 문제 발생 시 이전 Artifact로 Rollback 가능한 상태다.

장애가 발견되면 [장애 대응 가이드](./incident-response.md)의 심각도와 런북을 사용합니다.
