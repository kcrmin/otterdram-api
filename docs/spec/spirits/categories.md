# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 9) Categories (admin)

| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/categories` | 카테고리 목록 | 관리자 | 읽기 |
| POST | `/v1/categories` | 생성 | 관리자 | N/A |
| GET | `/v1/categories/{id}` | 상세 | 관리자 | 읽기 |
| PUT | `/v1/categories/{id}` | 전체 업데이트 | 관리자 | 멱등 |
| PATCH | `/v1/categories/{id}` | 부분 업데이트 | 관리자 | 비멱등 |
| DELETE | `/v1/categories/{id}` | 삭제(소프트) | 관리자 | 멱등 |

#### Query (`GET /v1/categories`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| parentId | long | 아니오 |  |  | FK |
| depth | smallint | 아니오 |  | 0,1,2… |  |
| path | string | 아니오 |  | prefix | materialized path |
| status | enum | 관리자 | CONFIRMED | DataStatus |  |


### 본문 스펙 — Categories
#### Representation
```json
{
  "id": 1,
  "name": "Single Malt",
  "parentId": null,
  "depth": 0,
  "path": "1",
  "translations": {},
  "descriptions": {},
  "status": "CONFIRMED",
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### CreateRequest
```json
{
  "name": "Single Malt"
}
```
#### UpdateRequest
```json
{
  "name": "Single Malt Scotch"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "name": "Single Malt",
  "parentId": null,
  "depth": 0,
  "path": "1",
  "translations": {},
  "descriptions": {},
  "status": "CONFIRMED",
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "name": "Single Malt",
      "parentId": null,
      "depth": 0,
      "path": "1",
      "translations": {},
      "descriptions": {},
      "status": "CONFIRMED",
      "createdAt": "2025-08-12T09:00:00Z"
    }
  ],
  "meta": {
    "page": 0,
    "size": 50,
    "totalElements": 1,
    "totalPages": 1
  },
  "links": {
    "self": "/v1/categories?page=0&size=50",
    "next": null,
    "prev": null
  }
}
```
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
