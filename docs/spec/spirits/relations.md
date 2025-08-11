# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 7 Relations (Admin)

### Distillery ↔ Brand
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/distillery-brand-relations` | 매핑 목록 | 필요 | 읽기 |
| POST | `/v1/distillery-brand-relations` | 매핑 생성 | 관리자 | N/A |
| DELETE | `/v1/distillery-brand-relations/{id}` | 매핑 삭제 | 관리자 | 멱등 |

#### Query
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| distilleryId | long | 아니오 |  |  | FK |
| brandId | long | 아니오 |  |  | FK |

### Release ↔ Distillery
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/release-distillery-relations` | 매핑 목록 | 필요 | 읽기 |
| POST | `/v1/release-distillery-relations` | 매핑 생성 | 관리자 | N/A |
| DELETE | `/v1/release-distillery-relations/{id}` | 매핑 삭제 | 관리자 | 멱등 |

#### Query
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| releaseId | long | 아니오 |  |  | FK |
| distilleryId | long | 아니오 |  |  | FK |


### 본문 스펙 — Distillery-Brand Relations
#### Representation
```json
{
  "id": 1,
  "distilleryId": 1,
  "brandId": 1,
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### CreateRequest
```json
{
  "distilleryId": 1,
  "brandId": 1
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "distilleryId": 1,
  "brandId": 1,
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "distilleryId": 1,
      "brandId": 1,
      "createdAt": "2025-08-12T09:00:00Z",
      "createdBy": 1
    }
  ],
  "meta": {
    "page": 0,
    "size": 50,
    "totalElements": 1,
    "totalPages": 1
  },
  "links": {
    "self": "/v1/distillery-brand-relations?page=0&size=50",
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


### 본문 스펙 — Release-Distillery Relations
#### Representation
```json
{
  "id": 1,
  "releaseId": 100,
  "distilleryId": 1,
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### CreateRequest
```json
{
  "releaseId": 100,
  "distilleryId": 1
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "releaseId": 100,
  "distilleryId": 1,
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "releaseId": 100,
      "distilleryId": 1,
      "createdAt": "2025-08-12T09:00:00Z",
      "createdBy": 1
    }
  ],
  "meta": {
    "page": 0,
    "size": 50,
    "totalElements": 1,
    "totalPages": 1
  },
  "links": {
    "self": "/v1/release-distillery-relations?page=0&size=50",
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