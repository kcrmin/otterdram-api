# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## Casks & Relations (Admin)

### Cask Materials
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/cask-materials` | 소재 목록 | 필요 | 읽기 |
| POST | `/v1/cask-materials` | 소재 생성 | 관리자 | N/A |
| PATCH | `/v1/cask-materials/{id}` | 수정 | 관리자 | 비멱등 |

### Cask Types
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/cask-types` | 규격 목록 | 필요 | 읽기 |
| POST | `/v1/cask-types` | 규격 생성 | 관리자 | N/A |
| PATCH | `/v1/cask-types/{id}` | 수정 | 관리자 | 비멱등 |

### Casks
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/casks` | 캐스크 목록 | 필요 | 읽기 |
| POST | `/v1/casks` | 캐스크 생성 | 관리자 | N/A |
| PATCH | `/v1/casks/{id}` | 수정 | 관리자 | 비멱등 |

### Release ↔ Casks
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/release-cask-relations` | 릴리즈-캐스크 목록 | 필요 | 읽기 |
| POST | `/v1/release-cask-relations` | 매핑 생성 | 관리자 | N/A |
| DELETE | `/v1/release-cask-relations/{id}` | 매핑 삭제 | 관리자 | 멱등 |


### 본문 스펙 — Casks
#### Representation
```json
{
  "id": 1,
  "name": "PX Sherry Butt",
  "categoryId": null,
  "materialId": 1,
  "shapeId": 2,
  "status": "CONFIRMED",
  "createdAt": "2025-08-12T09:00:00Z",
  "updatedAt": "2025-08-12T09:00:00Z",
  "deletedAt": null
}
```
#### CreateRequest
```json
{
  "name": "PX Sherry Butt",
  "materialId": 1,
  "shapeId": 2
}
```
#### UpdateRequest
```json
{
  "categoryId": 3
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "name": "PX Sherry Butt",
  "categoryId": null,
  "materialId": 1,
  "shapeId": 2,
  "status": "CONFIRMED",
  "createdAt": "2025-08-12T09:00:00Z",
  "updatedAt": "2025-08-12T09:00:00Z",
  "deletedAt": null
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "name": "PX Sherry Butt",
      "categoryId": null,
      "materialId": 1,
      "shapeId": 2,
      "status": "CONFIRMED",
      "createdAt": "2025-08-12T09:00:00Z",
      "updatedAt": "2025-08-12T09:00:00Z",
      "deletedAt": null
    }
  ],
  "meta": {
    "page": 0,
    "size": 50,
    "totalElements": 1,
    "totalPages": 1
  },
  "links": {
    "self": "/v1/casks?page=0&size=50",
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


### 본문 스펙 — Release-Cask Relations
#### Representation
```json
{
  "id": 1,
  "releaseId": 100,
  "fillNumber": "FIRST_FILL",
  "caskId": 1,
  "proportion": 50.0,
  "maturationMonths": 18,
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### CreateRequest
```json
{
  "releaseId": 100,
  "caskId": 1,
  "fillNumber": "FIRST_FILL",
  "proportion": 50.0,
  "maturationMonths": 18
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "releaseId": 100,
  "fillNumber": "FIRST_FILL",
  "caskId": 1,
  "proportion": 50.0,
  "maturationMonths": 18,
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
      "fillNumber": "FIRST_FILL",
      "caskId": 1,
      "proportion": 50.0,
      "maturationMonths": 18,
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
    "self": "/v1/release-cask-relations?page=0&size=50",
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