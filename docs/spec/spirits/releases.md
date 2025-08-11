# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 6 Releases

| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/releases` | 목록 | 필요 | 읽기 |
| POST | `/v1/releases` | 생성 | 관리자 | N/A |
| GET | `/v1/releases/{id}` | 상세 | 필요 | 읽기 |
| PATCH | `/v1/releases/{id}` | 수정 | 관리자 | 비멱등 |
| DELETE | `/v1/releases/{id}` | 삭제(소프트) | 관리자 | 멱등 |

#### Query (`GET /v1/releases`)
| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| modelId | long | 아니오 |  |  | FK |
| distilleryId | long | 아니오 |  |  | 조인 |
| ageStatementType | enum | 아니오 |  | AGE_STATED\|NAS\|UNAGED\|UNKNOWN |
| statedAge[gte\|lte] | smallint | 아니오 |  |  |  |
| abv[gte\|lte] | decimal | 아니오 |  |  |  |
| bottlingStrengthType | enum | 아니오 |  | CASK_STRENGTH\|FULL_PROOF\|OVERPROOF\|STANDARD\|UNDERPROOF |
| limitedEdition | tri-state | 아니오 |  | true\|false\|unknown |
| peatLevel | enum | 아니오 |  | NONE\|LIGHT\|MEDIUM\|HEAVY\|EXTREME\|UNKNOWN |
| status | enum | 관리자 | CONFIRMED | DataStatus |
| q | string | 아니오 |  |  | 이름/번역 |


### 본문 스펙 — Releases
#### Representation
```json
{
  "id": 100,
  "modelId": 50,
  "releaseName": "Spring 2025",
  "ageStatementType": "NAS",
  "statedAge": null,
  "distilledOn": null,
  "bottledOn": null,
  "bottlingStrengthType": "STANDARD",
  "abv": 46.0,
  "limitedEdition": null,
  "releasedBottles": null,
  "bottlingFormatType": "UNKNOWN",
  "chillFiltered": null,
  "naturalColor": null,
  "peatLevel": "UNKNOWN",
  "status": "CONFIRMED",
  "createdAt": "2025-08-12T09:00:00Z",
  "updatedAt": "2025-08-12T09:00:00Z",
  "deletedAt": null
}
```
- 규칙: `AGE_STATED` → `statedAge>0`; 그 외 → `statedAge=null`
- `0<abv≤100`
#### CreateRequest
```json
{
  "modelId": 50,
  "releaseName": "Spring 2025",
  "ageStatementType": "NAS",
  "statedAge": null,
  "abv": 46.0,
  "bottlingStrengthType": "STANDARD",
  "limitedEdition": null,
  "peatLevel": "UNKNOWN"
}
```
#### UpdateRequest
```json
{
  "releaseName": "Spring 2025 (Rev A)",
  "abv": 48.0,
  "limitedEdition": true
}
```
#### 단건 성공 예시 (200)
```json
{
  "id": 100,
  "modelId": 50,
  "releaseName": "Spring 2025",
  "ageStatementType": "NAS",
  "statedAge": null,
  "distilledOn": null,
  "bottledOn": null,
  "bottlingStrengthType": "STANDARD",
  "abv": 46.0,
  "limitedEdition": null,
  "releasedBottles": null,
  "bottlingFormatType": "UNKNOWN",
  "chillFiltered": null,
  "naturalColor": null,
  "peatLevel": "UNKNOWN",
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
      "id": 100,
      "modelId": 50,
      "releaseName": "Spring 2025",
      "ageStatementType": "NAS",
      "statedAge": null,
      "distilledOn": null,
      "bottledOn": null,
      "bottlingStrengthType": "STANDARD",
      "abv": 46.0,
      "limitedEdition": null,
      "releasedBottles": null,
      "bottlingFormatType": "UNKNOWN",
      "chillFiltered": null,
      "naturalColor": null,
      "peatLevel": "UNKNOWN",
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
    "self": "/v1/releases?page=0&size=50",
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