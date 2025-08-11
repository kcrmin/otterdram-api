# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 7) Vials & Transfers

### Vials
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/vials` | 바이알 목록 | 필요 | 읽기 |
| POST | `/v1/vials` | 바이알 추가 | 필요 | N/A |
| GET | `/v1/vials/{id}` | 바이알 상세 | 필요 | 읽기 |
| PUT | `/v1/vials/{id}` | 전체 업데이트 | 필요 | 멱등 |
| PATCH | `/v1/vials/{id}` | 부분 업데이트 | 필요 | 비멱등 |
| DELETE | `/v1/vials/{id}` | 삭제(소프트) | 필요 | 멱등 |

#### Query (`GET /v1/vials`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| shelfId | long | 아니오 |  |  | FK |
| releaseId | long | 아니오 |  |  | FK |
| isBlind | bool | 아니오 |  | true\|false |  |
| completed | bool | 아니오 |  | true\|false |  |
| vialSize[gte\|lte] | int | 아니오 |  |  |  |
| vialSizeUnit | enum | 아니오 | ML | VolumeUnit |  |

### Vial Transfers
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/vial-transfers` | 이관 목록 | 필요 | 읽기 |
| POST | `/v1/vial-transfers` | 이관 생성 | 필요 | N/A |
| GET | `/v1/vial-transfers/{id}` | 이관 상세 | 필요 | 읽기 |
| PATCH | `/v1/vial-transfers/{id}` | 상태 변경 | 필요 | 비멱등 |

#### Query (`GET /v1/vial-transfers`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| vialId | long | 아니오 |  |  | FK |
| status | enum | 아니오 | ACTIVE | ACTIVE\|CLAIMED\|EXPIRED\|REVOKED |  |
| transferedAt[gte\|lte] | datetime | 아니오 |  |  |  |

### Vial Transfer Tokens
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/vial-transfer-tokens` | 토큰 목록 | 필요 | 읽기 |
| POST | `/v1/vial-transfer-tokens` | 토큰 발급 | 필요 | N/A |
| GET | `/v1/vial-transfer-tokens/{id}` | 토큰 상세 | 필요 | 읽기 |
| DELETE | `/v1/vial-transfer-tokens/{id}` | 폐기 | 필요 | 멱등 |

#### Query (`GET /v1/vial-transfer-tokens`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| vialTransferId | long | 아니오 |  |  | FK |
| claimType | enum | 아니오 |  | QR_CODE\|NFC\|CODE |  |
| expiresAt[gte\|lte] | datetime | 아니오 |  |  |  |


### 본문 스펙 — Vials
#### Representation
```json
{
  "id": 1,
  "shelfId": 1,
  "releaseId": 100,
  "vialName": "Sample A",
  "isBlind": false,
  "vialSize": 30,
  "vialSizeUnit": "ML",
  "completed": false,
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### CreateRequest
```json
{
  "shelfId": 1,
  "releaseId": 100,
  "vialName": "Sample A",
  "vialSize": 30,
  "vialSizeUnit": "ML"
}
```
#### UpdateRequest
```json
{
  "completed": true
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "shelfId": 1,
  "releaseId": 100,
  "vialName": "Sample A",
  "isBlind": false,
  "vialSize": 30,
  "vialSizeUnit": "ML",
  "completed": false,
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
      "releaseId": 100,
      "vialName": "Sample A",
      "isBlind": false,
      "vialSize": 30,
      "vialSizeUnit": "ML",
      "completed": false,
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
    "self": "/v1/vials?page=0&size=50",
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


### 본문 스펙 — Vial Transfers
#### Representation
```json
{
  "id": 1,
  "vialId": 1,
  "status": "ACTIVE",
  "transferedAt": null,
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### CreateRequest
```json
{
  "vialId": 1
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "vialId": 1,
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
      "vialId": 1,
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
    "self": "/v1/vial-transfers?page=0&size=50",
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


### 본문 스펙 — Vial Transfer Tokens
#### Representation
```json
{
  "id": 1,
  "vialTransferId": 1,
  "claimType": "QR_CODE",
  "claimToken": "****",
  "expiresAt": "2025-12-31T00:00:00Z"
}
```
#### CreateRequest
```json
{
  "vialTransferId": 1,
  "claimType": "QR_CODE"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "vialTransferId": 1,
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
      "vialTransferId": 1,
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
    "self": "/v1/vial-transfer-tokens?page=0&size=50",
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
