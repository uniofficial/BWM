# BWM Frontend 장애 대응 가이드

## 1. 목적과 원칙

이 문서는 운영 중 프론트 화면, API 통신, 인증, 입찰, 지갑, 상품 등록에서 문제가 발생했을 때 빠르게 범위를 좁히고 안전하게 복구하기 위한 인수인계 문서입니다.

1. 사용자 영향과 데이터 변경 위험을 먼저 멈춥니다.
2. 변경 API는 성공 여부를 모르면 반복 호출하지 않습니다.
3. Token, Cookie, 비밀번호, 요청 본문, 지갑·사용자 전체 응답을 기록하거나 공유하지 않습니다.
4. 확정된 사실과 추정을 구분합니다.
5. 배포 Commit, 발생 시각, Route, Endpoint, Status, Error code, Request ID를 기준으로 프론트와 백엔드 범위를 나눕니다.

## 2. 심각도

| 등급 | 기준 | 초기 대응 |
|---|---|---|
| SEV-1 | 전체 접속 불가, 로그인 전면 불가, 광범위한 잘못된 결제·입찰·잔액 | 즉시 변경 중지, Rollback·트래픽 차단 검토, 전 파트 공유 |
| SEV-2 | 핵심 기능 다수 실패, 특정 사용자군 인증 불가, 중복 변경 가능성 | 15분 내 담당자 지정, 영향 기능 임시 중단 검토 |
| SEV-3 | 일부 Route·브라우저·기능 실패, 우회 가능 | 업무 시간 내 분석과 수정 계획 공유 |
| SEV-4 | 문구·스타일·낮은 빈도 오류, 핵심 흐름 영향 없음 | 일반 결함으로 등록 |

## 3. 초기 접수 양식

```text
발생 시각(시간대 포함):
사용 환경(브라우저/OS/기기):
접속 Route:
수행한 행동:
기대 결과:
실제 결과:
재현 여부:
HTTP Method / Endpoint / Status:
Error code / Request ID:
배포 Version / Commit SHA:
영향 사용자 범위:
데이터 변경 가능성:
첨부: 민감정보 제거한 화면 또는 HAR
```

비밀번호, Access Token, Cookie 값, Authorization Header, 회원·지갑 전체 응답은 받지 않습니다. HAR는 Cookie와 Header를 제거한 뒤 공유합니다.

## 4. 오류 분류

프론트 `ApiError`와 전역 진단 레코드는 다음 분류를 사용합니다.

| Category | 의미 | 우선 확인 |
|---|---|---|
| `FRONTEND_RUNTIME` | React Render 또는 처리되지 않은 Promise 오류 | Console, Commit, 재현 Route |
| `ROUTING` | Route·정적 파일·API 404 | 요청 주체와 응답 Content-Type |
| `CONFIGURATION` | API Origin 등 Build 설정 오류 | 배포 환경 변수와 재Build 여부 |
| `NETWORK` | HTTP 응답을 받지 못함 | 인터넷, DNS, TLS, CORS, 서버 상태 |
| `CORS` | 브라우저가 교차 Origin 응답 차단 | Console, OPTIONS, 허용 Origin |
| `AUTHENTICATION` | 401, Token·Refresh 문제 | Authorization, Cookie, Refresh 횟수 |
| `AUTHORIZATION` | 403, 권한 부족 | 사용자 역할과 리소스 소유권 |
| `VALIDATION` | 400·422 입력 오류 | 필드 값과 Error code |
| `BUSINESS_CONFLICT` | 409 또는 현재 상태 충돌 | 최신 상품·입찰·지갑 상태 |
| `SERVER` | 5xx | Request ID와 서버 로그 |
| `UPLOAD` | 파일 크기·형식·Multipart 문제 | 413·415, Proxy 제한, boundary |
| `STALE_DATA` | Polling·경합으로 화면이 오래된 상태 | 요청 순서, 최신 응답, 서버 상태 |
| `UNKNOWN` | 아직 분류되지 않은 오류 | 최소 재현과 원본 발생 지점 |

브라우저는 CORS 실패를 Axios에 `NETWORK`로 전달할 수 있습니다. Console에 CORS 문구가 확인된 경우 운영 기록에서 `CORS`로 재분류합니다.

## 5. 공통 진단 순서

1. 영향 범위와 심각도를 정합니다.
2. 최근 프론트·백엔드·Proxy·환경 변수 변경을 확인합니다.
3. 새 시크릿 창에서 동일 Route를 재현합니다.
4. Network에서 실패 요청의 Method, Endpoint, Status, Request ID를 확인합니다.
5. Console에서 `[BWM operational error]`의 안전 필드를 확인합니다.
6. 읽기 요청은 한 번 다시 시도하고, 변경 요청은 서버 반영 여부부터 확인합니다.
7. 프론트만 문제면 이전 정적 Artifact Rollback을 우선 검토합니다.
8. API·DB 상태 문제면 해당 기능의 변경 진입을 막고 백엔드 담당자에게 전달합니다.

## 6. 공통 런북

### 6.1 흰 화면·렌더 오류

징후: 화면이 비어 있거나 앱 오류 화면이 표시됩니다.

- Console에서 첫 Runtime 오류와 `commitSha`를 확인합니다.
- `index.html`이 최신인데 JS Asset이 이전 Cache인지 확인합니다.
- 특정 Route에서만 발생하면 Route Param과 API 응답 형태를 확인합니다.
- 전체 사용자에게 새 배포 직후 발생하면 이전 정적 Artifact로 Rollback합니다.
- Error Boundary는 화면 복구 수단이며 오류 자체를 해결하거나 자동 보고하는 외부 APM은 아닙니다.

### 6.2 Route 404·Asset 404

- 주소창 직접 접근만 실패하면 SPA fallback 설정을 확인합니다.
- `/assets/*.js`가 HTML을 반환하면 Rewrite 제외 규칙을 확인합니다.
- API 404는 응답 Content-Type과 API Origin으로 정적 404와 구분합니다.
- Chunk 404가 새 배포 직후 발생하면 `index.html` Cache와 이전 Asset 정리 정책을 확인합니다.

### 6.3 Network·CORS·Mixed Content

- `status`가 없으면 서버 다운으로 단정하지 않습니다.
- Console의 CORS, TLS, DNS, Mixed Content 메시지를 확인합니다.
- OPTIONS의 `Access-Control-Allow-Origin`, `Allow-Credentials`, Method, Header를 확인합니다.
- 허용 Origin은 Scheme·Host·Port까지 실제 프론트 Origin과 같아야 합니다.
- HTTPS 프론트에서 HTTP API·이미지를 호출하면 양쪽 모두 HTTPS로 교정합니다.

### 6.4 401 반복·Refresh 실패

- 원 요청 401 뒤 `/api/auth/reissue`가 최대 한 번인지 확인합니다.
- 여러 요청이 동시에 401이어도 Refresh 호출은 공유 Promise 하나여야 합니다.
- Refresh Cookie의 존재 여부와 속성만 확인하고 값은 보지 않습니다.
- Refresh 응답 `Authorization: Bearer ...` Header 노출을 확인합니다.
- 재시도한 원 요청도 401이면 인증 상태를 제거하고 로그인으로 전환하는 것이 정상입니다.
- 무한 루프가 보이면 해당 배포를 Rollback하고 `_retry`, `_skipAuthRefresh` 변경을 확인합니다.

### 6.5 403·409

- 403은 로그인 실패가 아니라 권한·소유권 문제일 수 있습니다.
- 409는 최신 서버 상태를 다시 조회한 뒤 사용자에게 충돌 이유를 안내합니다.
- 입찰·경매 종료·회원 탈퇴처럼 경합이 있는 작업은 자동 재시도하지 않습니다.

### 6.6 서버 5xx

- 사용자에게 서버 내부 메시지나 Stack을 노출하지 않습니다.
- Request ID가 있으면 함께 전달합니다.
- 현재 백엔드는 Request ID를 발급하지 않으므로 발생 시각, Endpoint, 사용자 영향 범위를 대신 기록합니다.
- 같은 변경 요청을 재실행하기 전에 DB 반영 여부를 백엔드가 확인합니다.

## 7. 기능별 런북

### 상품 목록·상세

Endpoint: `GET /api/items/search`, `GET /api/items/{id}`

- Query와 Path의 상품 ID를 확인합니다.
- 목록만 실패하면 검색 Param·정렬·Pagination 응답을 확인합니다.
- 상세만 실패하면 삭제·종료된 상품 여부와 404를 확인합니다.
- 이미지 실패는 API 응답의 상대 경로, API Origin, HTTPS, Storage 접근을 분리해 확인합니다.

### 상품 상세 Polling

- 현재 간격은 5초이며 요청 완료 후 다음 `setTimeout`을 예약합니다.
- 이전 요청은 `AbortController`로 취소하고 최신 요청 ID만 화면에 반영합니다.
- 탭이 숨겨지면 Polling을 멈추고 다시 보일 때 재개합니다.
- 경매 상태가 `OPEN`이 아니면 Polling을 종료합니다.
- 지연된 응답이 최신 화면을 덮는다면 `latestRequestId`와 Route 변경 cleanup을 확인합니다.
- 장애 중 Polling이 5xx를 증폭시키면 프론트 Rollback 또는 백엔드 Rate limit·임시 차단을 검토합니다.

### 입찰

Endpoint: `POST /api/items/{id}/bids`, `POST /api/items/{id}/bids/quick`

- 일반 입찰과 빠른 입찰이 하나의 in-flight 잠금을 공유해 중복 클릭을 막는지 확인합니다.
- 빠른 입찰은 요청 본문 없이 서버가 요청 시점의 최소 입찰 금액을 계산합니다.
- Timeout·Network 오류면 같은 금액을 바로 다시 보내지 않습니다.
- 상품 상세, 내 입찰 내역, 현재 최고가에서 반영 여부를 확인합니다.
- 409는 종료·최고가 변경 등 비즈니스 경합으로 처리합니다.
- 중복 요청 방지를 위한 서버 Idempotency Key는 현재 확인되지 않았습니다.

### 상품 등록·이미지 업로드

Endpoint: `POST /api/items`, `POST /api/items/{id}/images`

- 상품 생성과 이미지 업로드는 두 단계이므로 부분 성공이 가능합니다.
- 이미지 실패 시 상품 ID가 생성됐는지 먼저 확인합니다.
- 413은 Proxy·서버 Body 제한, 415는 MIME·확장자·파일 Signature를 확인합니다.
- Multipart `Content-Type`을 수동 지정하지 않습니다.
- 실패한 파일의 정리·재업로드 정책은 백엔드와 추가 합의가 필요합니다.

### 지갑·충전 요청

Endpoint: `GET /api/wallets/me`, `GET /api/wallets/me/histories`, `POST /api/wallets/charge/requests`

- 충전 요청 Timeout 뒤에는 요청 내역에서 생성 여부를 먼저 확인합니다.
- 잔액 불일치는 거래 내역의 마지막 잔액과 서버 원장을 기준으로 백엔드가 확인합니다.
- 프론트 화면 값만 보고 금액을 보정하지 않습니다.
- 충전 요청의 Idempotency 보장은 현재 확인되지 않았습니다.

### 경매 종료

Endpoint: `POST /api/items/{id}/close`

- 판매자·상품 상태·최고 입찰 여부를 확인합니다.
- Timeout 뒤에는 상품 상세와 내 상품 상태를 재조회합니다.
- 성공 응답의 `itemId`, `status`가 불완전하면 서버 반영 여부를 확인합니다.
- 종료 요청은 자동 재시도하지 않습니다.

### 로그아웃·회원 탈퇴

Endpoint: `POST /api/auth/logout`, `DELETE /api/auth/withdraw`

- 로그아웃은 `POST /api/auth/logout` 호출로 서버의 Refresh Token을 즉시 폐기한 뒤 클라이언트 상태를 지웁니다. 이 요청이 실패(오프라인 등)해도 클라이언트 로그아웃 자체는 항상 진행됩니다 — 사용자가 로그아웃 버튼을 눌렀는데 화면이 로그인 상태로 남는 것을 방지하기 위함입니다.
- 회원 탈퇴는 `DELETE /api/auth/withdraw`가 성공한 뒤에만 클라이언트 상태를 지우고 이동합니다. 요청이 실패하면 계정은 그대로 유지되고 화면에 실패 사유를 표시합니다.
- 진행 중인 경매의 판매자이거나 최고 입찰자인 경우 백엔드가 409로 거부합니다(`IllegalStateException` → `CONFLICT`). 이 메시지를 그대로 사용자에게 보여줍니다.
- 탈퇴 요청이 Timeout으로 응답 확인이 안 되면 같은 요청을 바로 재시도하지 않고 재로그인 가능 여부로 실제 탈퇴 여부를 확인합니다.

## 8. 즉시 완화와 Rollback

### 프론트 Rollback 기준

- 새 Commit에서만 SEV-1 또는 SEV-2가 재현됩니다.
- 전체 렌더 실패, Asset 경로 오류, 인증 무한 루프가 발생합니다.
- 입찰·충전·경매 종료의 중복 요청 가능성이 새로 생겼습니다.

### 절차

1. 신규 배포와 관련 변경을 중지합니다.
2. 마지막 정상 Commit·Artifact를 식별합니다.
3. 정적 Artifact를 이전 버전으로 교체합니다.
4. `index.html` Cache가 갱신됐는지 확인합니다.
5. 읽기 Smoke Test와 인증 진입을 확인합니다.
6. 변경 요청은 서버 데이터 상태 확인 후 최소 범위로 검증합니다.

환경 변수만 바꿔도 Vite Bundle은 재Build해야 합니다. 서버 장애를 프론트 Rollback만으로 해결할 수는 없습니다.

## 9. 담당 경계

| 현상 | 1차 담당 | 협업 |
|---|---|---|
| Render, Route, Asset, UI 상태 | 프론트 | 인프라 |
| API Contract, 4xx·5xx, Request ID | 백엔드 | 프론트 |
| CORS, Cookie 발급·만료 | 백엔드 | 프론트·인프라 |
| DNS, TLS, CDN, Cache, Rewrite | 인프라 | 프론트·백엔드 |
| 입찰·지갑·경매 데이터 정합성 | 백엔드 | 프론트 |
| 사용자 안내와 영향 범위 | 기능 담당 | 전 파트 |

현재 최우선 백엔드 운영 준비 항목은 Production CORS·Cookie 분리 설정, Health Check, Request ID 발급·로그 연계, 변경 API의 멱등성 정책 확정입니다.

## 10. 종료와 사후 회고

장애 종료 조건:

- 사용자 영향이 멈췄습니다.
- 핵심 읽기·인증 Smoke Test가 통과합니다.
- 데이터 변경 요청의 중복·누락 여부를 확인했습니다.
- 원인 또는 다음 진단 책임자가 정해졌습니다.
- 임시 조치의 제거 일정이 있습니다.

### 장애 보고 템플릿

```text
제목:
심각도:
발생/탐지/완화/종료 시각:
영향 범위:
사용자 증상:
원인:
기여 요인:
탐지 방법:
즉시 조치:
데이터 정합성 확인:
재발 방지 작업(담당/기한):
배포 Version / Commit:
관련 Request ID:
```

사후 회고에서는 개인이 아니라 탐지 지연, 안전장치, 테스트 공백, 배포 절차, API 계약을 개선 대상으로 삼습니다.
