# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 1) Conventions & Policies (한국어, TDD 체크 포함)

### 1.1 전송, 미디어, 시간/로케일
- Base URL: `/v1`
- `Content-Type: application/json`, `Accept: application/json`
- 시간: ISO 8601 **UTC** (`YYYY-MM-DDTHH:mm:ssZ`), 클라에서 현지표시
- 언어: `lang={LanguageCode}`

### 1.2 인증/권한
- Bearer JWT 기본
- 역할: `ROLE_USER`, `ROLE_ADMIN`
- 실패: 401/403

### 1.3 소프트 삭제
- `deletedAt/by` 설정, 기본 조회 제외
- 관리자만 `includeDeleted=true`

### 1.4 GEO 읽기 전용
- GEO 리소스는 GET/HEAD/OPTIONS만
- 변형 메서드 405 + `Allow`

### 1.5 페이지/정렬/필터
- `page` 0-base, `size` ≤ 200
- `sort=field,asc|desc` 반복 허용
- 범위/IN/검색 `q` 제공

### 1.6 캐싱/레이트리밋
- `ETag`/`Last-Modified`, 조건부 304
- 초과 429 + `X-RateLimit-*`

### 1.7 멱등성
- PUT/DELETE 멱등
- 민감 POST는 `Idempotency-Key`

### 1.8 에러 포맷(공통)
### 오류 응답 예시
```json
{
  "error": "VALIDATION_ERROR",
  "message": "field is required",
  "details": [
    {
      "field": "field",
      "code": "NotBlank"
    }
  ],
  "traceId": "req-..."
}
```

### 1.9 문서/스키마
- 표는 키워드 중심, 긴 설명은 본문
- 모든 목록 GET에 Query 표 제공
- `X-Schema-Version: 1.0.0`

### 1.10 관리자 전용 CUD
- Companies/Distilleries/Brands/Collections/Models/Releases
- Distillery↔Brand, Release↔Distillery, Casks(및 Master) and relations
- 조회 허용, 삭제는 소프트

### 1.11 응답 포맷(권장)
- 키: camelCase, enum UPPER_SNAKE, tri-state: `true|false|null`, 시간: ISO UTC
- 목록: `data[] + meta + links`, 생성 201 + `Location`
