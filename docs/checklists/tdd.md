# OtterDram — TDD 테스트 체크리스트 (단일 파일)
_생성: 2025-08-11 23:27:51_

## 1) Conventions & Policies (공통)
- [ ] `Content-Type` JSON 아님 → 415
- [ ] `Accept` JSON 불일치 → 406
- [ ] 모든 날짜/시간 ISO 8601 UTC(Z) 직렬화/파싱
- [ ] 목록 응답 스키마 보장: data[], meta{page,size,totalElements,totalPages}, links{self,next,prev}
- [ ] 응답 헤더 존재: X-Request-Id, X-Schema-Version
- [ ] 캐싱 동작: ETag/If-None-Match, Last-Modified/If-Modified-Since → 304
- [ ] 레이트리밋 초과 → 429 + X-RateLimit-*
- [ ] 페이징 유효성: page(0-base), size≤200 (초과 시 400 또는 클램핑 정책)
- [ ] 정렬: 복수 sort=field,dir 순서 보장
- [ ] 미지원 쿼리 파라미터 처리 일관(무시 또는 400)
- [ ] 에러 바디 포맷 일관(error/message/details/traceId) 및 상태코드 매핑(400/401/403/404/405/409/410/422/429)
- [ ] 멱등성: PUT/DELETE 멱등, 민감 POST는 Idempotency-Key 재시도 시 동일 결과

## 2) 권한/보안
- [ ] 인증 필요한 API 미인증 → 401
- [ ] 권한 부족(롤/스코프) → 403
- [ ] GEO 변형 메서드(POST/PUT/PATCH/DELETE) → 405 + Allow: GET, HEAD, OPTIONS
- [ ] 관리자 전용 CUD: Spirits 계층, Casks, Relations, Categories/Roles/UserRoles (일반 유저 403)

## 3) 소프트 삭제 (Idempotent DELETE)
- [ ] DELETE 성공 → 204 & deletedAt/by 세팅
- [ ] 기본 목록/단건에서 삭제 항목 미노출(일반 단건은 404)
- [ ] 관리자 + includeDeleted=true 시 삭제 항목 노출
- [ ] 동일 리소스 DELETE 재호출도 204

## 4) 공통 쿼리 파라미터 계약 (모든 목록 GET)
- [ ] page/size/sort/q/ids/createdAt[gte|lte]/updatedAt[gte|lte] 동작
- [ ] includeDeleted는 관리자만 허용(일반 사용자가 지정 시 403 또는 400)
- [ ] 범위+IN+검색 동시 적용 및 SQL 인젝션 방어

## 5) Users
- [ ] POST /v1/users → 201 + Location: /v1/users/{id}
- [ ] username/email 유니크 충돌 → 409
- [ ] email 형식 오류 → 400
- [ ] 목록 필터 동작: status/verified/role/username/email/lastLoginAt[gte|lte]
- [ ] 소프트 삭제 가시성(일반/관리자) 규칙

### 5.1 User Profiles
- [ ] displayName, birthday 미지정 → 400
- [ ] gender 미지정 시 UNSPECIFIED 적용
- [ ] 목록 필터 동작: userId/displayName/countryId/cityId/gender

### 5.2 User Social Accounts
- [ ] (provider, providerId) 중복 → 409
- [ ] 생성/조회/삭제 정상 흐름

### 5.3 Roles (Admin)
- [ ] Role CUD 관리자만 / 조회 권한 확인
- [ ] roleName 유니크 충돌 → 409

### 5.4 User Roles (Admin)
- [ ] (userId, roleId) 중복 → 409
- [ ] 삭제 멱등 204 반복

### 5.5 Follows
- [ ] 생성 201, 기본 status 정책 세팅
- [ ] 승인/거절 PATCH 유효값 외 → 400
- [ ] 목록 필터 동작: followerId/followeeId/status

### 5.6 Blocks
- [ ] 차단 생성 201, 중복 차단 처리(409 또는 멱등 204 정책)
- [ ] 목록 필터 동작: blockerId/blockedId

## 6) Reports (Common)
### 6.1 User Reports
- [ ] 생성 201
- [ ] 필수 reportedUserId/languageCode/reportReason 누락 → 400
- [ ] 상태 변경(PATCH) 관리자만 / 부적합 전이 → 422

### 6.2 Content Reports
- [ ] 생성 201
- [ ] targetType/targetId/languageCode/reportReason 검증
- [ ] 미존재 대상 → 404

## 7) UGC
### 7.1 Shelves
- [ ] 생성/조회/수정/삭제(소프트)
- [ ] privacy 기본값/변경 검증
- [ ] 목록 필터 동작: ownerId/privacy/shelfName

### 7.2 Shelf Share Tokens
- [ ] 토큰 발급 201
- [ ] claimType 유효성
- [ ] 상태 변경 유효값 외 → 400

### 7.3 Bottles
- [ ] 필수 shelfId/releaseId/bottleSize/bottleSizeUnit 누락 → 400
- [ ] bottleSizeUnit/residualVolumeUnit enum 유효성
- [ ] opened 전이 시 firstOpenedAt/lastOpenedAt 처리
- [ ] 목록 필터 동작: shelfId/releaseId/opened/completed/residualVolume[gte|lte]/bottleSize[gte|lte]/(units)

### 7.4 Bottle Transfers
- [ ] 생성 201, 상태 변경(PATCH)
- [ ] 목록 필터 동작: bottleId/status/transferedAt[gte|lte]

### 7.5 Bottle Transfer Tokens
- [ ] 발급 201, claimType 유효
- [ ] (bottleTransferId, claimType) 중복 → 409
- [ ] expiresAt 지난 토큰 사용 → 410(정책)

### 7.6 Vials
- [ ] 필수 shelfId/releaseId/vialName/vialSize 누락 → 400
- [ ] vialSizeUnit enum 유효성
- [ ] isBlind/completed 전이 규칙
- [ ] 목록 필터 동작: shelfId/releaseId/isBlind/completed/vialSize[gte|lte]/(units)

### 7.7 Vial Transfers / Tokens
- [ ] 이관 생성 201 / 상태 변경
- [ ] 토큰 발급 201 / (vialTransferId, claimType) 중복 → 409

### 7.8 Reviews
- [ ] 필수/enum 유효성: releaseId/tier/alcoholPresence/complexity/bodyIntensity/finishLength/balance/servingStyle
- [ ] score 0–100.0, 소수 1자리 → 400
- [ ] 목록 필터 동작: privacy/languageCode/blindTasting/servingStyle/tier/score[gte|lte]

### 7.9 Comments
- [ ] 필수 entityType/entityId/comment 누락 → 400
- [ ] depth 0/1 외 값 → 400
- [ ] 목록 필터 동작: entityType/entityId/parentId/depth/languageCode

### 7.10 Comment Votes
- [ ] 생성 201
- [ ] (entityCommentId, createdBy) 중복 → 409
- [ ] 삭제 멱등 204

## 8) Spirits (Admin CUD)
### 8.1 Categories
- [ ] CUD 관리자만
- [ ] name 필수, depth/path 일관성
- [ ] 목록 필터 동작: parentId/depth/path/status

### 8.2 Companies
- [ ] CUD 관리자만 / companyName 유니크 → 409
- [ ] 목록 필터 동작: independentBottler/status/q

### 8.3 Distilleries
- [ ] CUD 관리자만 / distilleryName 유니크 → 409
- [ ] 목록 필터 동작: companyId/countryId/cityId/operationalStatus/status/q

### 8.4 Brands
- [ ] CUD 관리자만 / brandName 유니크 → 409
- [ ] 목록 필터 동작: companyId/status/q

### 8.5 Collections
- [ ] CUD 관리자만 / collectionName 필수
- [ ] 목록 필터 동작: brandId/status/q

### 8.6 Models
- [ ] CUD 관리자만 / modelName 필수
- [ ] 목록 필터 동작: collectionId/categoryId/status/q

### 8.7 Releases
- [ ] CUD 관리자만
- [ ] AGE_STATED ⇒ statedAge > 0; 그 외 ⇒ statedAge == null
- [ ] 0 < abv ≤ 100
- [ ] enum 유효성: ageStatementType/bottlingStrengthType/peatLevel/bottlingFormatType
- [ ] 목록 필터 동작: modelId/distilleryId/ageStatementType/statedAge[gte|lte]/abv[gte|lte]/limitedEdition/peatLevel/status/q

### 8.8 Relations (Admin)
- [ ] Distillery↔Brand 중복 매핑 → 409, FK 무결성 위반 → 404/422
- [ ] Release↔Distillery 규칙 동일

### 8.9 Casks & Relations (Admin)
- [ ] Cask Materials/Types/Casks CUD 관리자만
- [ ] casks.name 유니크 → 409
- [ ] Release↔Cask: proportion 합계 ≤ 100 (초과 시 422)
- [ ] maturationMonths ≥ 0, fillNumber enum 유효

### 8.10 Entity Revisions
- [ ] 동일 엔티티 isLatest=true 항상 1개 유지
- [ ] APPROVED 시 메인 반영 / REJECTED 비노출
- [ ] 부적합 상태 전이 → 422

### 8.11 Entity Tags
- [ ] 필수 entityType/entityId/languageCode/tag 누락 → 400
- [ ] 동일 (entityType,entityId,languageCode,tag) 중복 → 409

## 9) GEO DATA (Read-only)
- [ ] 각 리소스 GET 정상 / HEAD, OPTIONS 응답 정상
- [ ] 변형 메서드 405 + 정확한 Allow
- [ ] 미존재 ID → 404
- [ ] 조건부 요청 304 동작

## 10) CI/품질 게이트
- [ ] 유닛/슬라이스/통합 테스트 분리 실행 프로파일
- [ ] 커버리지 임계값(예: 80%) 미달 시 PR 차단
- [ ] 공통 정책 변경 시 관련 테스트 자동 실행(예: 페이지 상한)
- [ ] 린터/포맷터/스키마 검증(JSON Schema, OpenAPI) 통과
