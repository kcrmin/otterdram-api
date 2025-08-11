# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 3) Users

### 요약
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/users` | 유저 목록 | 필요 | 읽기 |
| POST | `/v1/users` | 유저 생성 | 필요 | N/A |
| GET | `/v1/users/{userId}` | 유저 상세 | 필요 | 읽기 |
| PUT | `/v1/users/{userId}` | 전체 업데이트 | 필요 | 멱등 |
| PATCH | `/v1/users/{userId}` | 부분 업데이트 | 필요 | 비멱등 |
| DELETE | `/v1/users/{userId}` | 삭제(소프트) | 필요 | 멱등 |

#### Query (`GET /v1/users`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| status | enum | 아니오 | ACTIVE | ACTIVE\|SUSPENDED\|BANNED\|DEACTIVATED | UserStatus |
| verified | bool | 아니오 |  | true\|false |  |
| role | string | 아니오 |  | ROLE_* |  |
| username | string | 아니오 |  |  | 완전/부분 |
| email | string | 아니오 |  |  | 완전/부분 |
| lastLoginAt[gte\|lte] | datetime | 아니오 |  | ISO 8601 |  |

### User Profiles
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/user-profiles` | 프로필 목록 | 필요 | 읽기 |
| POST | `/v1/user-profiles` | 프로필 생성 | 필요 | N/A |
| GET | `/v1/user-profiles/{id}` | 프로필 상세 | 필요 | 읽기 |
| PUT | `/v1/user-profiles/{id}` | 전체 업데이트 | 필요 | 멱등 |
| PATCH | `/v1/user-profiles/{id}` | 부분 업데이트 | 필요 | 비멱등 |
| DELETE | `/v1/user-profiles/{id}` | 삭제(소프트) | 필요 | 멱등 |

#### Query (`GET /v1/user-profiles`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| userId | long | 아니오 |  |  | FK |
| displayName | string | 아니오 |  |  |  |
| countryId | long | 아니오 |  |  | FK |
| cityId | long | 아니오 |  |  | FK |
| gender | enum | 아니오 |  | MALE\|FEMALE\|RATHER_NOT_SAY\|UNSPECIFIED\|OTHER |  |

### User Social Accounts
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/user-social-accounts` | 소셜 목록 | 필요 | 읽기 |
| POST | `/v1/user-social-accounts` | 연동 | 필요 | N/A |
| GET | `/v1/user-social-accounts/{id}` | 상세 | 필요 | 읽기 |
| DELETE | `/v1/user-social-accounts/{id}` | 해제 | 필요 | 멱등 |

#### Query (`GET /v1/user-social-accounts`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| userId | long | 아니오 |  |  | FK |
| provider | string | 아니오 |  |  | ex) google, apple |
| providerId | string | 아니오 |  |  |  |

### User Settings
- 단건만: `GET /v1/user-settings/{userId}`

### Roles & User Roles
#### Roles
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/roles` | 롤 목록 | 관리자 | 읽기 |
| POST | `/v1/roles` | 롤 생성 | 관리자 | N/A |
| GET | `/v1/roles/{roleId}` | 롤 상세 | 관리자 | 읽기 |
| PUT | `/v1/roles/{roleId}` | 전체 업데이트 | 관리자 | 멱등 |
| DELETE | `/v1/roles/{roleId}` | 삭제 | 관리자 | 멱등 |

#### Query (`GET /v1/roles`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| roleName | string | 아니오 |  |  |  |
| createdBy | long | 아니오 |  |  | FK |

#### User Roles (매핑)
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/user-roles` | 매핑 목록 | 관리자 | 읽기 |
| POST | `/v1/user-roles` | 매핑 추가 | 관리자 | N/A |
| DELETE | `/v1/user-roles/{id}` | 매핑 제거 | 관리자 | 멱등 |

#### Query (`GET /v1/user-roles`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| userId | long | 아니오 |  |  | FK |
| roleId | long | 아니오 |  |  | FK |

### Follows & Blocks
#### Follows
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/user-follows` | 팔로우 목록 | 필요 | 읽기 |
| POST | `/v1/user-follows` | 팔로우 요청 | 필요 | N/A |
| PATCH | `/v1/user-follows/{id}` | 승인/거절 | 필요 | 비멱등 |
| DELETE | `/v1/user-follows/{id}` | 언팔/취소 | 필요 | 멱등 |

#### Query (`GET /v1/user-follows`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| followerId | long | 아니오 |  |  | FK |
| followeeId | long | 아니오 |  |  | FK |
| status | enum | 아니오 | APPROVED | APPROVED\|PENDING\|REJECTED |  |

#### Blocks
| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/user-blocks` | 차단 목록 | 필요 | 읽기 |
| POST | `/v1/user-blocks` | 차단 | 필요 | N/A |
| DELETE | `/v1/user-blocks/{id}` | 해제 | 필요 | 멱등 |

#### Query (`GET /v1/user-blocks`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| blockerId | long | 아니오 |  |  | FK |
| blockedId | long | 아니오 |  |  | FK |


### 본문 스펙 — Users
#### Representation
```json
{
  "id": 1,
  "username": "otter",
  "email": "otter@example.com",
  "profileImage": null,
  "verified": false,
  "userPrivacy": "PUBLIC",
  "userStatus": "ACTIVE",
  "lastLoginAt": "2025-08-12T09:00:00Z",
  "createdAt": "2025-08-12T09:00:00Z",
  "updatedAt": "2025-08-12T09:00:00Z",
  "deletedAt": null
}
```
#### CreateRequest
```json
{
  "username": "otter",
  "email": "otter@example.com"
}
```
#### UpdateRequest
```json
{
  "userPrivacy": "FOLLOWERS_ONLY",
  "verified": true
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "username": "otter",
  "email": "otter@example.com",
  "profileImage": null,
  "verified": false,
  "userPrivacy": "PUBLIC",
  "userStatus": "ACTIVE",
  "lastLoginAt": "2025-08-12T09:00:00Z",
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
      "username": "otter",
      "email": "otter@example.com",
      "profileImage": null,
      "verified": false,
      "userPrivacy": "PUBLIC",
      "userStatus": "ACTIVE",
      "lastLoginAt": "2025-08-12T09:00:00Z",
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
    "self": "/v1/users?page=0&size=50",
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


### 본문 스펙 — User Profiles
#### Representation
```json
{
  "id": 10,
  "userId": 1,
  "displayName": "Otter Dram",
  "bio": "whisky geek",
  "countryId": 410,
  "cityId": 100,
  "address": null,
  "birthday": "1990-01-01",
  "gender": "UNSPECIFIED",
  "createdAt": "2025-08-12T09:00:00Z",
  "updatedAt": "2025-08-12T09:00:00Z",
  "deletedAt": null
}
```
#### CreateRequest
```json
{
  "userId": 1,
  "displayName": "Otter Dram",
  "birthday": "1990-01-01"
}
```
- 필수: `displayName`, `birthday` / `gender` 생략 가능(UNSPECIFIED)
#### UpdateRequest
```json
{
  "bio": "single malt lover",
  "gender": "OTHER"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 10,
  "userId": 1,
  "displayName": "Otter Dram",
  "bio": "whisky geek",
  "countryId": 410,
  "cityId": 100,
  "address": null,
  "birthday": "1990-01-01",
  "gender": "UNSPECIFIED",
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
      "id": 10,
      "userId": 1,
      "displayName": "Otter Dram",
      "bio": "whisky geek",
      "countryId": 410,
      "cityId": 100,
      "address": null,
      "birthday": "1990-01-01",
      "gender": "UNSPECIFIED",
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
    "self": "/v1/user-profiles?page=0&size=50",
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


### 본문 스펙 — User Social Accounts
#### Representation
```json
{
  "id": 1,
  "userId": 1,
  "provider": "google",
  "providerId": "abc123",
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### CreateRequest
```json
{
  "userId": 1,
  "provider": "google",
  "providerId": "abc123"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "userId": 1,
  "provider": "google",
  "providerId": "abc123",
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "userId": 1,
      "provider": "google",
      "providerId": "abc123",
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
    "self": "/v1/user-social-accounts?page=0&size=50",
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


### 본문 스펙 — Roles
#### Representation
```json
{
  "id": 1,
  "roleName": "ROLE_ADMIN",
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1,
  "updatedAt": "2025-08-12T09:00:00Z",
  "updatedBy": 1
}
```
#### CreateRequest
```json
{
  "roleName": "ROLE_REVIEWER"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "roleName": "ROLE_ADMIN",
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1,
  "updatedAt": "2025-08-12T09:00:00Z",
  "updatedBy": 1
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "roleName": "ROLE_ADMIN",
      "createdAt": "2025-08-12T09:00:00Z",
      "createdBy": 1,
      "updatedAt": "2025-08-12T09:00:00Z",
      "updatedBy": 1
    }
  ],
  "meta": {
    "page": 0,
    "size": 50,
    "totalElements": 1,
    "totalPages": 1
  },
  "links": {
    "self": "/v1/roles?page=0&size=50",
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


### 본문 스펙 — User Roles
#### Representation
```json
{
  "id": 1,
  "userId": 1,
  "roleId": 1,
  "createdAt": "2025-08-12T09:00:00Z",
  "createdBy": 1
}
```
#### CreateRequest
```json
{
  "userId": 1,
  "roleId": 1
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "userId": 1,
  "roleId": 1,
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
      "userId": 1,
      "roleId": 1,
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
    "self": "/v1/user-roles?page=0&size=50",
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


### 본문 스펙 — Follows
#### Representation
```json
{
  "id": 1,
  "followerId": 1,
  "followeeId": 2,
  "status": "PENDING",
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### CreateRequest
```json
{
  "followeeId": 2
}
```
#### UpdateRequest (승인/거절)
```json
{
  "status": "APPROVED"
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "followerId": 1,
  "followeeId": 2,
  "status": "PENDING",
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "followerId": 1,
      "followeeId": 2,
      "status": "PENDING",
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
    "self": "/v1/user-follows?page=0&size=50",
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


### 본문 스펙 — Blocks
#### Representation
```json
{
  "id": 1,
  "blockerId": 1,
  "blockedId": 2,
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### CreateRequest
```json
{
  "blockedId": 2
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 1,
  "blockerId": 1,
  "blockedId": 2,
  "createdAt": "2025-08-12T09:00:00Z"
}
```
#### 목록 성공 예시 (200)
```json
{
  "data": [
    {
      "id": 1,
      "blockerId": 1,
      "blockedId": 2,
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
    "self": "/v1/user-blocks?page=0&size=50",
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
