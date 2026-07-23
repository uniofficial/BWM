# BWM Frontend

React, Vite, TypeScript, Tailwind CSS로 구현된 경매 서비스 프론트엔드입니다.

## Requirements

- Node.js `^20.19.0 || >=22.12.0`
- npm과 `package-lock.json`
- 개발 백엔드 `http://localhost:8080`

## Local development

`.env.development`는 Git에 커밋되지 않으므로(`.gitignore`) 저장소를 새로 받았다면 먼저 직접 만들어야 합니다. `frontend/.env.development` 파일을 아래 내용으로 생성하세요.

```dotenv
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_ENV=development
```

API Endpoint에 `/api`가 포함되어 있으므로 Base URL에는 `/api`를 붙이지 않습니다. 이 파일이 없으면 실행은 되지만 "앱 설정을 확인해주세요" 화면만 표시됩니다.

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

운영 빌드 전에 배포 플랫폼에 실제 `VITE_API_BASE_URL`을 HTTPS Origin으로 등록해야 합니다. 자세한 환경 변수와 배포 방법은 [배포 가이드](./docs/deployment.md), 배포 전후 확인은 [운영 체크리스트](./docs/operations-checklist.md), 장애 진단은 [장애 대응 가이드](./docs/incident-response.md)를 참고하세요.

## Known limitations

- 실제 Production API 주소와 정적 호스팅 플랫폼은 아직 확정되지 않았습니다.
