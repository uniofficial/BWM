# BWM Frontend

React, Vite, TypeScript, Tailwind CSS로 구현된 경매 서비스 프론트엔드입니다.

## Requirements

- Node.js `^20.19.0 || >=22.12.0`
- npm과 `package-lock.json`
- 개발 백엔드 `http://localhost:8080`

## Local development

`.env.development`는 로컬 API Origin을 사용합니다. API Endpoint에 `/api`가 포함되어 있으므로 Base URL에는 `/api`를 붙이지 않습니다.

```bash
npm ci
npm run dev
```

개발 서버 기본 주소는 `http://localhost:5173`입니다.

## Quality checks

```bash
npm run lint
npm run build
npm run preview
```

운영 빌드 전에 배포 플랫폼에 실제 `VITE_API_BASE_URL`을 HTTPS Origin으로 등록해야 합니다. 자세한 환경 변수, SPA Route, Cookie, CORS, 캐시 및 배포 후 점검 방법은 [배포 가이드](./docs/deployment.md)를 참고하세요.

## Known limitations

- 백엔드 로그아웃 API가 없어 로그아웃은 클라이언트 상태만 제거합니다.
- 회원 탈퇴는 백엔드 계약 확정 전까지 임시 로그아웃으로 처리합니다.
- 실제 Production API 주소와 정적 호스팅 플랫폼은 아직 확정되지 않았습니다.
