# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 6) Bottles & Transfers

### Bottles
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/bottles` | 병 목록 | 필요 | 읽기 |
| POST | `/v1/bottles` | 병 추가 | 필요 | N/A |
| GET | `/v1/bottles/{id}` | 병 상세 | 필요 | 읽기 |
| PUT | `/v1/bottles/{id}` | 전체 업데이트 | 필요 | 멱등 |
| PATCH | `/v1/bottles/{id}` | 부분 업데이트 | 필요 | 비멱등 |
| DELETE | `/v1/bottles/{id}` | 삭제(소프트) | 필요 | 멱등 |

#### Query (`GET /v1/bottles`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| shelfId | long | 아니오 |  |  | FK |
| releaseId | long | 아니오 |  |  | FK |
| opened | bool | 아니오 |  | true\|false |  |
| completed | bool | 아니오 |  | true\|false |  |
| residualVolume[gte\|lte] | int | 아니오 |  |  |  |
| bottleSize[gte\|lte] | int | 아니오 |  |  |  |
| bottleSizeUnit | enum | 아니오 | ML | VolumeUnit |  |
| residualVolumeUnit | enum | 아니오 | ML | VolumeUnit |  |

### Bottle Transfers
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/bottle-transfers` | 이관 목록 | 필요 | 읽기 |
| POST | `/v1/bottle-transfers` | 이관 생성 | 필요 | N/A |
| GET | `/v1/bottle-transfers/{id}` | 이관 상세 | 필요 | 읽기 |
| PATCH | `/v1/bottle-transfers/{id}` | 상태 변경 | 필요 | 비멱등 |

#### Query (`GET /v1/bottle-transfers`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| bottleId | long | 아니오 |  |  | FK |
| status | enum | 아니오 | ACTIVE | ACTIVE\|CLAIMED\|EXPIRED\|REVOKED |  |
| transferedAt[gte\|lte] | datetime | 아니오 |  |  |  |

### Bottle Transfer Tokens
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/bottle-transfer-tokens` | 토큰 목록 | 필요 | 읽기 |
| POST | `/v1/bottle-transfer-tokens` | 토큰 발급 | 필요 | N/A |
| GET | `/v1/bottle-transfer-tokens/{id}` | 토큰 상세 | 필요 | 읽기 |
| DELETE | `/v1/bottle-transfer-tokens/{id}` | 폐기 | 필요 | 멱등 |

#### Query (`GET /v1/bottle-transfer-tokens`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| bottleTransferId | long | 아니오 |  |  | FK |
| claimType | enum | 아니오 |  | QR_CODE\|NFC\|CODE |  |
| expiresAt[gte\|lte] | datetime | 아니오 |  |  |  |


### 본문 스펙 — Bottles
#### Representation
```json
{
  "id": 1,
  "shelfId": 1,
  "releaseId": 100,
  "note": null,
  "residualVolume": 500,
  "residualVolumeUnit": "ML",
  "bottleSize": 700,
  "bottleSizeUnit": "ML",
  "acquiredAt": null,
  "opened": false,
  "firstOpenedAt": null,
  "lastOpenedAt": null,
  "completed": false,
  "createdAt": "2025-08-12T09:00:00Z",
  "updatedAt": "2025-08-12T09:00:00Z",
  "deletedAt": null
}
```
#### CreateRequest
```json
{
  "shelfId": 1,
  "releaseId": 100,
  "bottleSize": 700,
  "bottleSizeUnit": "ML"
}
```
#### UpdateRequest
```json
{
  "opened": true,
  "firstOpenedAt": "2025-08-12T09:00:00Z",
  "residualVolume": 300
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "shelfId": 1,
  "releaseId": 100,
  "note": null,
  "residualVolume": 500,
  "residualVolumeUnit": "ML",
  "bottleSize": 700,
  "bottleSizeUnit": "ML",
  "acquiredAt": null,
  "opened": false,
  "firstOpenedAt": null,
  "lastOpenedAt": null,
  "completed": false,
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
      "shelfId": 1,
      "releaseId": 100,
      "note": null,
      "residualVolume": 500,
      "residualVolumeUnit": "ML",
      "bottleSize": 700,
      "bottleSizeUnit": "ML",
      "acquiredAt": null,
      "opened": false,
      "firstOpenedAt": null,
      "lastOpenedAt": null,
      "completed": false,
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
    "self": "/v1/bottles?page=0&size=50",
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


### 본문 스펙 — Bottle Transfers
#### Representation
```json
{
  "id": 1,
  "bottleId": 1,
  "status": "ACTIVE",
  "transferedAt": null,
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### CreateRequest
```json
{
  "bottleId": 1
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "bottleId": 1,
  "status": "ACTIVE",
  "transferedAt": null,
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
      "bottleId": 1,
      "status": "ACTIVE",
      "transferedAt": null,
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
    "self": "/v1/bottle-transfers?page=0&size=50",
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


### 본문 스펙 — Bottle Transfer Tokens
#### Representation
```json
{
  "id": 1,
  "bottleTransferId": 1,
  "claimType": "QR_CODE",
  "claimToken": "****",
  "expiresAt": "2025-12-31T00:00:00Z"
}
```
#### CreateRequest
```json
{
  "bottleTransferId": 1,
  "claimType": "QR_CODE"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "bottleTransferId": 1,
  "claimType": "QR_CODE",
  "claimToken": "****",
  "expiresAt": "2025-12-31T00:00:00Z"
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "bottleTransferId": 1,
      "claimType": "QR_CODE",
      "claimToken": "****",
      "expiresAt": "2025-12-31T00:00:00Z"
    }
  ],
  "meta": {
    "page": 0,
    "size": 50,
    "totalElements": 1,
    "totalPages": 1
  },
  "links": {
    "self": "/v1/bottle-transfer-tokens?page=0&size=50",
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
