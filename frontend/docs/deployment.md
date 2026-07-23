# BWM Frontend Deployment Guide

관련 문서: [운영 체크리스트](./operations-checklist.md) · [장애 대응 가이드](./incident-response.md)

## 1. 배포 전제

- Node.js: `^20.19.0 || >=22.12.0`
- 패키지 매니저: npm
- 설치: `npm ci`
- Build: `npm run build -- --mode production`
- 정적 결과물: `dist/`
- Router: React Router `BrowserRouter`
- 기본 배포 경로: 도메인 루트 `/`

Vite 환경 변수는 런타임이 아니라 **Build 시점**에 Bundle에 포함됩니다. 환경 변수를 변경한 뒤에는 반드시 새 Build와 재배포가 필요합니다.

## 2. 환경 변수

| 변수 | 필수 | 설명 |
|---|---:|---|
| `VITE_API_BASE_URL` | 예 | 백엔드 Origin. `/api`를 포함하지 않습니다. |
| `VITE_APP_ENV` | 아니요 | `development`, `production`, `test`. 생략하면 Vite mode를 사용합니다. |
| `VITE_APP_VERSION` | 아니요 | 배포 Release 식별자. 운영 오류 진단에 포함됩니다. |
| `VITE_COMMIT_SHA` | 아니요 | 배포 Commit SHA. 운영 오류 진단에 포함됩니다. |

개발 예시:

```dotenv
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_ENV=development
```

운영 설정:

```dotenv
# TODO: 실제 운영 API Origin으로 교체
VITE_API_BASE_URL=https://<production-api-origin>
VITE_APP_ENV=production
```

다음 값은 허용하지 않습니다.

- 빈 `VITE_API_BASE_URL`
- 상대 URL
- `/api` 같은 Path가 포함된 Base URL
- 운영 Build의 HTTP URL
- 운영 Build의 `localhost`, `127.0.0.1`, `::1`
- 사용자명, 비밀번호, Query, Fragment가 포함된 URL

검증에 실패하면 앱은 API 요청을 시작하지 않고 설정 오류 화면을 표시합니다. `VITE_` 변수는 공개 Bundle에 들어가므로 JWT Secret, DB 비밀번호, Token 또는 클라우드 Secret을 넣지 않습니다.

## 3. API URL 규칙

Base URL은 Origin만 담당하고, 각 API 모듈의 Endpoint가 `/api`부터 담당합니다.

```text
Base URL: https://api.example.com
Endpoint: /api/items/search
Result:   https://api.example.com/api/items/search
```

Axios 일반 API와 인증 API 모두 중앙 환경 설정을 사용하고 `withCredentials: true`를 유지합니다. Multipart 요청에는 `Content-Type`을 직접 지정하지 않아 브라우저가 boundary를 생성합니다. 전역 Timeout은 실제 운영 네트워크 기준이 없으므로 설정하지 않았습니다.

백엔드가 `/images/items/1/example.jpg` 같은 상대 이미지 경로를 반환하면 API Origin과 결합합니다. 절대 HTTPS 이미지 URL은 그대로 사용합니다.

## 4. Production Build

배포 플랫폼의 Production 환경 변수에 실제 API Origin을 등록한 후 실행합니다.

```bash
npm ci
npm run lint
npm run build -- --mode production
```

현재 `.env.production`의 API 주소는 의도적으로 비어 있습니다. 실제 주소를 확인하지 않은 상태에서 예시 도메인으로 배포하지 마세요.

Build 정책:

- TypeScript 검사 포함
- Vite `base: "/"`
- Production Source Map 비활성화
- 해시가 포함된 JS·CSS Asset 생성
- 별도 API 로그 없음

하위 경로 배포가 확정되면 Vite `base`와 `BrowserRouter basename`을 함께 변경하고 전체 Route를 다시 검증해야 합니다. 현재는 루트 배포이므로 basename을 사용하지 않습니다.

## 5. Preview

```bash
npm run preview -- --host 127.0.0.1 --port 4174
```

Vite Preview는 Build 결과 확인용이며 실제 호스팅의 Rewrite, Cache Header, CORS를 대신 검증하지 않습니다. Preview Origin을 백엔드 CORS에 임시 추가하지 않으면 API 요청은 실패할 수 있습니다.

## 6. SPA Route fallback

`BrowserRouter`를 사용하므로 정적 호스팅 서버는 파일이 없는 Route 요청을 `/index.html`로 Rewrite해야 합니다. `/assets/*`와 Reverse Proxy로 사용하는 `/api/*`는 fallback에서 제외합니다.

Vercel을 선택한 경우의 개념 예시:

```json
{
  "rewrites": [
    { "source": "/((?!assets/|api/).*)", "destination": "/index.html" }
  ]
}
```

Netlify를 선택한 경우 `_redirects` 개념 예시:

```text
/* /index.html 200
```

Nginx를 선택한 경우:

```nginx
location / {
    try_files $uri $uri/ /index.html;
}

location /api/ {
    proxy_pass http://backend:8080;
}
```

실제 플랫폼이 확정되지 않아 플랫폼별 설정 파일은 저장소에 추가하지 않았습니다. 하나의 플랫폼이 정해지면 해당 설정만 추가합니다.

## 7. CORS와 Refresh Cookie

프론트 인증 구조:

- Access Token: JavaScript Memory
- Refresh Token: 백엔드가 발급하는 HttpOnly Cookie
- API 요청: `withCredentials: true`

백엔드 CORS 필수 조건:

```text
Allowed Origin: 정확한 Production 프론트 Origin
Allowed Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Allowed Headers: Accept, Authorization, Content-Type
Exposed Headers: Authorization
Allow Credentials: true
```

Credentials를 사용할 때 `Access-Control-Allow-Origin: *`은 사용할 수 없습니다.

권장 Cookie 조건:

| 배포 구조 | SameSite | Secure | 비고 |
|---|---|---:|---|
| `example.com` + `api.example.com` | `Lax` 또는 정책에 맞는 값 | 예 | 같은 Site로 분류 가능한 권장 구조 |
| 서로 다른 Site | `None` | 예 | 정확한 CORS Origin과 HTTPS 필수 |
| Reverse Proxy `/api` | `Lax` 또는 `Strict` 검토 | 예 | CORS와 Cookie 구성이 가장 단순 |

공통으로 `HttpOnly=true`, 적절한 `Path`, 운영 HTTPS가 필요합니다. Cookie Domain은 실제 도메인 구조가 정해진 뒤 백엔드에서 설정합니다.

현재 백엔드는 개발용 `Secure=false`, `SameSite=Strict`를 사용하므로 운영 배포 전에 환경별 Cookie 설정이 필요합니다.

## 8. HTTPS와 Mixed Content

운영 프론트가 HTTPS이면 API와 사용자 업로드 이미지도 HTTPS여야 합니다. HTTPS 페이지에서 HTTP API 또는 이미지를 요청하면 브라우저가 Mixed Content로 차단합니다.

CSP를 적용할 때 실제 도메인이 확정된 뒤 다음 Source를 구성합니다.

- `connect-src`: API Origin
- `img-src`: `'self'`, `data:`, `blob:`, 이미지 Origin 또는 CDN
- `script-src`, `style-src`, `font-src`: 실제 사용 리소스

현재 API·CDN 도메인이 확정되지 않았으므로 완성된 CSP를 코드에 강제 적용하지 않습니다.

## 9. 정적 호스팅 Header와 Cache

권장 Cache 정책:

```text
/assets/*    Cache-Control: public, max-age=31536000, immutable
/index.html  Cache-Control: no-cache
```

권장 보안 Header:

```text
X-Content-Type-Options: nosniff
Referrer-Policy: strict-origin-when-cross-origin
Permissions-Policy: camera=(), microphone=(), geolocation=()
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

HSTS는 모든 하위 도메인의 HTTPS 준비가 끝난 뒤 적용합니다. CSP는 실제 API·이미지·폰트 도메인 확인 후 추가합니다.

## 10. 이미지 업로드 운영 조건

프론트 제한:

- JPG, PNG, WebP
- 파일당 10MB 이하
- 요청 전체 50MB 이하
- 다중 파일 필드명 `images`
- 대표 인덱스 필드명 `representativeIndex`

백엔드 Multipart와 Reverse Proxy Body 제한도 최소 동일해야 합니다. 운영에서는 서버 MIME·파일 Signature 검증, 업로드 Timeout, Storage URL, HTTPS, 파일 저장 실패 cleanup을 추가 확인합니다.

## 11. Known limitations

### Production 인프라

- 실제 Production API Origin 미확정
- 정적 호스팅 플랫폼 미확정
- 운영 CORS·Cookie·HTTPS 미검증
- 운영 이미지 Storage·CDN 미확정

## 12. 배포 후 Smoke Test

1. `/` 접속 후 상품 목록 이동
2. `/products` 직접 접근 및 새로고침
3. `/products/{validId}` 직접 접근 및 새로고침
4. 존재하지 않는 Route에서 Not Found 확인
5. 로그인 후 원래 보호 Route 복귀
6. `/products/new` 보호 여부와 상품 등록
7. 상품 상세와 이미지 fallback
8. 입찰 성공 후 상세 즉시 갱신 및 Polling
9. `/mypage/wallet` 지갑 조회
10. 충전 요청 및 요청 내역 갱신
11. `/mypage/products` 경매 종료
12. `/mypage/bids` 내 입찰 조회
13. Access Token 만료 후 Refresh와 원 요청 1회 재시도
14. 로그아웃(재로그인 시 세션 제거)과 회원 탈퇴(경매·입찰 중 탈퇴 제한 메시지 포함) 확인
15. 브라우저 Console·Network·OPTIONS·Cookie 확인

## 13. 최종 배포 체크리스트

- [ ] 실제 Production API HTTPS Origin 등록
- [ ] 프론트 Production Origin을 백엔드 CORS에 등록
- [ ] 운영 Refresh Cookie의 Secure·SameSite·Domain·Path 확인
- [ ] 선택한 호스팅 플랫폼에 SPA fallback 적용
- [ ] `/assets/*`와 `/api/*`를 SPA fallback에서 제외
- [ ] `index.html`과 해시 Asset Cache 정책 적용
- [ ] Proxy와 백엔드 Multipart 크기 제한 확인
- [ ] `npm ci`, lint, Production build 성공
- [ ] 직접 Route·새로고침·보호 Route 확인
- [ ] 로그인·Refresh·Mutation 성공 E2E 확인
- [ ] Source Map 미생성 확인
- [ ] Secret이 Bundle과 Git에 없는지 확인
