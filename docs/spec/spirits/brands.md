# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 3 Brands

| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/brands` | 목록 | 필요 | 읽기 |
| POST | `/v1/brands` | 생성 | 관리자 | N/A |
| GET | `/v1/brands/{id}` | 상세 | 필요 | 읽기 |
| PATCH | `/v1/brands/{id}` | 수정 | 관리자 | 비멱등 |
| DELETE | `/v1/brands/{id}` | 삭제(소프트) | 관리자 | 멱등 |

#### Query (`GET /v1/brands`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| companyId | long | 아니오 |  |  | FK |
| status | enum | 관리자 | CONFIRMED | DataStatus |
| q | string | 아니오 |  |  | 이름/번역 |


### 본문 스펙 — Brands
#### Representation
```json
{
  "id": 1,
  "name": "Brand",
  "status": "CONFIRMED",
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### CreateRequest
```json
{
  "name": "Brand"
}
```
#### UpdateRequest
```json
{
  "name": "Brand RevA"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "name": "Brand",
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
      "name": "Brand",
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
    "self": "/v1/brands?page=0&size=50",
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