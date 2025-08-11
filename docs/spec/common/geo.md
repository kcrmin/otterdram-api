# OtterDram Public API Spec (DBML-aligned, Production-Ready)
**Version:** 1.0.0 • **Date:** 2025-08-12 (Asia/Seoul)  
**Scope:** DBML 스키마와 1:1로 매핑된 프로덕션 API 사양.  
**Notes:** GEO 데이터는 읽기 전용, 위스키 구조 CUD는 관리자 전용, 소프트 삭제 적용.

## 13) GEO DATA (Read-only)

### Regions / Subregions / Countries / States / Cities
- 읽기 전용(GET/HEAD/OPTIONS). 변형 메서드 405.

| 메서드 | 경로 | 설명 | 인증 | 멱등성 |
|---|---|---|---|---|
| GET | `/v1/geo/regions` | 지역 목록 | 불필요 | 읽기 |
| GET | `/v1/geo/regions/{id}` | 지역 상세 | 불필요 | 읽기 |
| GET | `/v1/geo/subregions` | 하위지역 목록 | 불필요 | 읽기 |
| GET | `/v1/geo/subregions/{id}` | 하위지역 상세 | 불필요 | 읽기 |
| GET | `/v1/geo/countries` | 국가 목록 | 불필요 | 읽기 |
| GET | `/v1/geo/countries/{id}` | 국가 상세 | 불필요 | 읽기 |
| GET | `/v1/geo/states` | 주/도 목록 | 불필요 | 읽기 |
| GET | `/v1/geo/states/{id}` | 주/도 상세 | 불필요 | 읽기 |
| GET | `/v1/geo/cities` | 도시 목록 | 불필요 | 읽기 |
| GET | `/v1/geo/cities/{id}` | 도시 상세 | 불필요 | 읽기 |
