# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 12) Entity Revisions / Tags

### Entity Revisions
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/entity-revisions` | 리비전 목록 | 관리자 | 읽기 |
| POST | `/v1/entity-revisions` | 리비전 생성 | 필요 | N/A |
| GET | `/v1/entity-revisions/{id}` | 리비전 상세 | 관리자 | 읽기 |
| POST | `/v1/entity-revisions/{id}/approve` | 승인 | 관리자 | N/A |
| POST | `/v1/entity-revisions/{id}/reject` | 반려 | 관리자 | N/A |

### Entity Tags
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/entity-tags` | 태그 목록 | 필요 | 읽기 |
| POST | `/v1/entity-tags` | 태그 추가 | 필요 | N/A |
| DELETE | `/v1/entity-tags/{id}` | 태그 제거 | 필요 | 멱등 |


### 본문 스펙 — Entity Revisions
#### Representation
```json
{
  "id": 1,
  "entityType": "RELEASE",
  "entityId": 100,
  "schemaVersion": "1.0",
  "revisionData": {
    "releaseName": "Rev A"
  },
  "diffData": null,
  "isLatest": true,
  "status": "IN_REVIEW",
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1,
  "reviewedAt": null,
  "reviewedBy": null
}
```
#### CreateRequest
```json
{
  "entityType": "RELEASE",
  "entityId": 100,
  "revisionData": {
    "releaseName": "Rev A"
  }
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "entityType": "RELEASE",
  "entityId": 100,
  "schemaVersion": "1.0",
  "revisionData": {
    "releaseName": "Rev A"
  },
  "diffData": null,
  "isLatest": true,
  "status": "IN_REVIEW",
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1,
  "reviewedAt": null,
  "reviewedBy": null
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "entityType": "RELEASE",
      "entityId": 100,
      "schemaVersion": "1.0",
      "revisionData": {
        "releaseName": "Rev A"
      },
      "diffData": null,
      "isLatest": true,
      "status": "IN_REVIEW",
      "createdAt": "2025-08-12T09:00:00Z",
      "createdBy": 1,
      "reviewedAt": null,
      "reviewedBy": null
    }
  ],
  "meta": {
    "page": 0,
    "size": 50,
    "totalElements": 1,
    "totalPages": 1
  },
  "links": {
    "self": "/v1/entity-revisions?page=0&size=50",
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


### 본문 스펙 — Entity Tags
#### Representation
```json
{
  "id": 1,
  "entityType": "MODEL",
  "entityId": 10,
  "languageCode": "EN",
  "tag": "sherry",
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### CreateRequest
```json
{
  "entityType": "MODEL",
  "entityId": 10,
  "languageCode": "EN",
  "tag": "sherry"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "entityType": "MODEL",
  "entityId": 10,
  "languageCode": "EN",
  "tag": "sherry",
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
      "entityType": "MODEL",
      "entityId": 10,
      "languageCode": "EN",
      "tag": "sherry",
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
    "self": "/v1/entity-tags?page=0&size=50",
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
