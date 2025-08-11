# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 8) Reviews / Comments / Votes

### Reviews
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/reviews` | 리뷰 목록 | 필요 | 읽기 |
| POST | `/v1/reviews` | 리뷰 작성 | 필요 | N/A |
| GET | `/v1/reviews/{id}` | 리뷰 상세 | 필요 | 읽기 |
| PUT | `/v1/reviews/{id}` | 전체 업데이트 | 필요 | 멱등 |
| PATCH | `/v1/reviews/{id}` | 부분 업데이트 | 필요 | 비멱등 |
| DELETE | `/v1/reviews/{id}` | 삭제(소프트) | 필요 | 멱등 |

#### Query (`GET /v1/reviews`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| releaseId | long | 아니오 |  |  | FK |
| vialId | long | 아니오 |  |  | FK |
| privacy | enum | 아니오 | PUBLIC | Privacy |
| languageCode | enum | 아니오 |  | LanguageCode |  |
| blindTasting | bool | 아니오 |  | true\|false |  |
| servingStyle | enum | 아니오 |  | ServingStyle |  |
| tier | enum | 아니오 |  | Tier |  |
| score[gte\|lte] | decimal | 아니오 |  |  | 0–100.0 |

### Comments
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/comments` | 댓글 목록 | 필요 | 읽기 |
| POST | `/v1/comments` | 댓글 작성 | 필요 | N/A |
| GET | `/v1/comments/{id}` | 댓글 상세 | 필요 | 읽기 |
| PATCH | `/v1/comments/{id}` | 수정 | 필요 | 비멱등 |
| DELETE | `/v1/comments/{id}` | 삭제(소프트) | 필요 | 멱등 |

#### Query (`GET /v1/comments`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| entityType | enum | 아니오 |  | BRAND\|COLLECTION\|MODEL | CommentTargetEntity |
| entityId | long | 아니오 |  |  | FK |
| parentId | long | 아니오 |  |  |  |
| depth | smallint | 아니오 |  | 0\|1 |  |
| languageCode | enum | 아니오 | EN | LanguageCode |  |

### Comment Votes
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/comment-votes` | 투표 목록 | 필요 | 읽기 |
| POST | `/v1/comment-votes` | 투표 등록 | 필요 | N/A |
| DELETE | `/v1/comment-votes/{id}` | 투표 취소 | 필요 | 멱등 |

#### Query (`GET /v1/comment-votes`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| entityCommentId | long | 아니오 |  |  | FK |
| createdBy | long | 아니오 |  |  | FK |
| voteType | enum | 아니오 |  | LIKE\|DISLIKE |  |


### 본문 스펙 — Reviews
#### Representation
```json
{
  "id": 1,
  "releaseId": 100,
  "privacy": "PUBLIC",
  "languageCode": "EN",
  "blindTasting": false,
  "servingStyle": "NEAT",
  "servingSize": 30,
  "servingSizeUnit": "ML",
  "tier": "DECENT",
  "score": 88.0,
  "alcoholPresence": "MEDIUM",
  "complexity": "HIGH",
  "bodyIntensity": "FULL",
  "finishLength": "LONG",
  "balance": "WELL_BALANCED",
  "overallDescription": null,
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### CreateRequest
```json
{
  "releaseId": 100,
  "privacy": "PUBLIC",
  "tier": "DECENT",
  "score": 88.0
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "releaseId": 100,
  "privacy": "PUBLIC",
  "languageCode": "EN",
  "blindTasting": false,
  "servingStyle": "NEAT",
  "servingSize": 30,
  "servingSizeUnit": "ML",
  "tier": "DECENT",
  "score": 88.0,
  "alcoholPresence": "MEDIUM",
  "complexity": "HIGH",
  "bodyIntensity": "FULL",
  "finishLength": "LONG",
  "balance": "WELL_BALANCED",
  "overallDescription": null,
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
      "privacy": "PUBLIC",
      "languageCode": "EN",
      "blindTasting": false,
      "servingStyle": "NEAT",
      "servingSize": 30,
      "servingSizeUnit": "ML",
      "tier": "DECENT",
      "score": 88.0,
      "alcoholPresence": "MEDIUM",
      "complexity": "HIGH",
      "bodyIntensity": "FULL",
      "finishLength": "LONG",
      "balance": "WELL_BALANCED",
      "overallDescription": null,
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
    "self": "/v1/reviews?page=0&size=50",
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


### 본문 스펙 — Comments
#### Representation
```json
{
  "id": 1,
  "entityType": "BRAND",
  "entityId": 10,
  "parentId": null,
  "depth": 0,
  "languageCode": "EN",
  "comment": "Nice",
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### CreateRequest
```json
{
  "entityType": "BRAND",
  "entityId": 10,
  "comment": "Nice"
}
```
#### UpdateRequest
```json
{
  "comment": "👍"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "entityType": "BRAND",
  "entityId": 10,
  "parentId": null,
  "depth": 0,
  "languageCode": "EN",
  "comment": "Nice",
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
      "entityType": "BRAND",
      "entityId": 10,
      "parentId": null,
      "depth": 0,
      "languageCode": "EN",
      "comment": "Nice",
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
    "self": "/v1/comments?page=0&size=50",
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


### 본문 스펙 — Comment Votes
#### Representation
```json
{
  "id": 1,
  "entityCommentId": 1,
  "voteType": "LIKE",
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### CreateRequest
```json
{
  "entityCommentId": 1,
  "voteType": "LIKE"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "entityCommentId": 1,
  "voteType": "LIKE",
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
      "entityCommentId": 1,
      "voteType": "LIKE",
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
    "self": "/v1/comment-votes?page=0&size=50",
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
