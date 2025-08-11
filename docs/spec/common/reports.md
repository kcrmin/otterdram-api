# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 4) Reports

### User Reports
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/user-reports` | 유저 신고 목록 | 관리자 | 읽기 |
| POST | `/v1/user-reports` | 유저 신고 등록 | 필요 | N/A |
| GET | `/v1/user-reports/{id}` | 유저 신고 상세 | 관리자 | 읽기 |
| PATCH | `/v1/user-reports/{id}` | 상태 변경 | 관리자 | 비멱등 |

#### Query (`GET /v1/user-reports`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| reporterId | long | 아니오 |  |  | FK |
| reportedUserId | long | 아니오 |  |  | FK |
| languageCode | enum | 아니오 |  | LanguageCode |  |
| status | enum | 아니오 | PENDING | PENDING\|APPROVED\|REJECTED |  |
| reportedAt[gte\|lte] | datetime | 아니오 |  |  |  |

### Content Reports
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/content-reports` | 콘텐츠 신고 목록 | 관리자 | 읽기 |
| POST | `/v1/content-reports` | 신고 등록 | 필요 | N/A |
| GET | `/v1/content-reports/{id}` | 신고 상세 | 관리자 | 읽기 |
| PATCH | `/v1/content-reports/{id}` | 상태 변경 | 관리자 | 비멱등 |

#### Query (`GET /v1/content-reports`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| reporterId | long | 아니오 |  |  | FK |
| targetType | enum | 아니오 |  | ReportTargetType |  |
| targetId | long | 아니오 |  |  |  |
| languageCode | enum | 아니오 |  | LanguageCode |  |
| status | enum | 아니오 | PENDING | PENDING\|APPROVED\|REJECTED |  |
| reportedAt[gte\|lte] | datetime | 아니오 |  |  |  |


### 본문 스펙 — User Reports
#### Representation
```json
{
  "id": 1,
  "reporterId": 1,
  "reportedUserId": 2,
  "languageCode": "EN",
  "reportReason": "spam",
  "reportDescription": null,
  "reportedAt": "2025-08-12T09:00:00Z",
  "status": "PENDING"
}
```
#### CreateRequest
```json
{
  "reportedUserId": 2,
  "languageCode": "EN",
  "reportReason": "spam"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "reporterId": 1,
  "reportedUserId": 2,
  "languageCode": "EN",
  "reportReason": "spam",
  "reportDescription": null,
  "reportedAt": "2025-08-12T09:00:00Z",
  "status": "PENDING"
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "reporterId": 1,
      "reportedUserId": 2,
      "languageCode": "EN",
      "reportReason": "spam",
      "reportDescription": null,
      "reportedAt": "2025-08-12T09:00:00Z",
      "status": "PENDING"
    }
  ],
  "meta": {
    "page": 0,
    "size": 50,
    "totalElements": 1,
    "totalPages": 1
  },
  "links": {
    "self": "/v1/user-reports?page=0&size=50",
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


### 본문 스펙 — Content Reports
#### Representation
```json
{
  "id": 1,
  "reporterId": 1,
  "targetType": "MODEL",
  "targetId": 10,
  "languageCode": "EN",
  "reportReason": "abuse",
  "reportDescription": null,
  "reportedAt": "2025-08-12T09:00:00Z",
  "status": "PENDING"
}
```
#### CreateRequest
```json
{
  "targetType": "MODEL",
  "targetId": 10,
  "languageCode": "EN",
  "reportReason": "abuse"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "reporterId": 1,
  "targetType": "MODEL",
  "targetId": 10,
  "languageCode": "EN",
  "reportReason": "abuse",
  "reportDescription": null,
  "reportedAt": "2025-08-12T09:00:00Z",
  "status": "PENDING"
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "reporterId": 1,
      "targetType": "MODEL",
      "targetId": 10,
      "languageCode": "EN",
      "reportReason": "abuse",
      "reportDescription": null,
      "reportedAt": "2025-08-12T09:00:00Z",
      "status": "PENDING"
    }
  ],
  "meta": {
    "page": 0,
    "size": 50,
    "totalElements": 1,
    "totalPages": 1
  },
  "links": {
    "self": "/v1/content-reports?page=0&size=50",
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
