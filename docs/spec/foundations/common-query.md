# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 2) Common Query (모든 목록 GET 공통)

| 이름 | 타입 | 필수 | 기본값 | 허용값/형식 | 비고 |
|---|---|---|---|---|---|
| page | int | 아니오 | 0 | ≥0 | 페이징 |
| size | int | 아니오 | 50 | 1–200 | 페이지 크기 |
| sort | string\|repeat | 아니오 | createdAt,desc | 필드,asc\|desc | 다중 정렬 |
| q | string | 아니오 |  |  | 부분 검색 |
| ids | array | 아니오 |  | CSV 또는 반복 | `id in (...)` |
| createdAt[gte\|lte] | datetime | 아니오 |  | ISO 8601 | 범위 |
| updatedAt[gte\|lte] | datetime | 아니오 |  | ISO 8601 | 범위 |
| includeDeleted | bool | 관리자 | false | true\|false | 소프트 삭제 포함 |
