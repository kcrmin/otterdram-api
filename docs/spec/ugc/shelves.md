# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 5) Shelves & Tokens

### Shelves
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/shelves` | 선반 목록 | 필요 | 읽기 |
| POST | `/v1/shelves` | 선반 생성 | 필요 | N/A |
| GET | `/v1/shelves/{shelfId}` | 선반 상세 | 필요 | 읽기 |
| PUT | `/v1/shelves/{shelfId}` | 전체 업데이트 | 필요 | 멱등 |
| PATCH | `/v1/shelves/{shelfId}` | 부분 업데이트 | 필요 | 비멱등 |
| DELETE | `/v1/shelves/{shelfId}` | 삭제(소프트) | 필요 | 멱등 |

#### Query (`GET /v1/shelves`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| ownerId | long | 아니오 |  |  | FK |
| privacy | enum | 아니오 | PUBLIC | Privacy |
| shelfName | string | 아니오 |  |  |  |

### Shelf Share Tokens
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/shelf-share-tokens` | 토큰 목록 | 필요 | 읽기 |
| POST | `/v1/shelf-share-tokens` | 토큰 발급 | 필요 | N/A |
| GET | `/v1/shelf-share-tokens/{id}` | 토큰 상세 | 필요 | 읽기 |
| PATCH | `/v1/shelf-share-tokens/{id}` | 상태 변경 | 필요 | 비멱등 |
| DELETE | `/v1/shelf-share-tokens/{id}` | 폐기 | 필요 | 멱등 |

#### Query (`GET /v1/shelf-share-tokens`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| shelfId | long | 아니오 |  |  | FK |
| claimType | enum | 아니오 |  | QR_CODE\|NFC\|CODE |  |
| status | enum | 아니오 | ACTIVE | ACTIVE\|CLAIMED\|EXPIRED\|REVOKED |  |


### 본문 스펙 — Shelves
#### Representation
```json
{
  "id": 1,
  "ownerId": 1,
  "privacy": "PUBLIC",
  "shelfName": "Top Shelf",
  "createdAt": "2025-08-12T09:00:00Z",
  "updatedAt": "2025-08-12T09:00:00Z",
  "deletedAt": null
}
```
#### CreateRequest
```json
{
  "ownerId": 1,
  "shelfName": "Top Shelf",
  "privacy": "PUBLIC"
}
```
#### UpdateRequest
```json
{
  "privacy": "PRIVATE",
  "shelfName": "Hidden Gems"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "ownerId": 1,
  "privacy": "PUBLIC",
  "shelfName": "Top Shelf",
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
      "ownerId": 1,
      "privacy": "PUBLIC",
      "shelfName": "Top Shelf",
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
    "self": "/v1/shelves?page=0&size=50",
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


### 본문 스펙 — Shelf Share Tokens
#### Representation
```json
{
  "id": 1,
  "shelfId": 1,
  "claimType": "QR_CODE",
  "claimToken": "****",
  "status": "ACTIVE",
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### CreateRequest
```json
{
  "shelfId": 1,
  "claimType": "QR_CODE"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "shelfId": 1,
  "claimType": "QR_CODE",
  "claimToken": "****",
  "status": "ACTIVE",
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "shelfId": 1,
      "claimType": "QR_CODE",
      "claimToken": "****",
      "status": "ACTIVE",
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
    "self": "/v1/shelf-share-tokens?page=0&size=50",
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
