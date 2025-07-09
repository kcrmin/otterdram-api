# 🌍 world.sql 기반 국가/지역 데이터 자동 반영 스크립트

이 스크립트는 [`world.sql`](https://github.com/dr5hn/countries-states-cities-database/blob/master/psql/world.sql) 데이터 파일을 PostgreSQL 임시 DB에 로드한 후, 프로드 DB의 `regions`, `subregions`, `countries`, `states`, `cities` 테이블을 **UPSERT** 방식으로 안전하게 복사합니다.

## 🚀 목적
* production DB의 외래키 무결성을 해치지 않고 안전히 업데이트
* [dr5hn/countries-states-cities-database](https://github.com/dr5hn/countries-states-cities-database)의 `world.sql`을 자동으로 복입
* Python 기반 자동화 + 로그 + 예외처리 포함

## ⚙️ 환경 설정
`.env.example` 파일을 참고해 환경 변수를 설정하세요.

## ⚠️ 주의사항
* `temp_db`는 실행 시 자동 삭제됩니다.
* `world.sql` 파일은 **수정하지 마세요.**
* 데이터 dump는 `public.` 스키마를 포함하고 있어 `search_path`로는 감지되지 않습니다.
* **UPSERT는 `id` 컬럼을 기본키로 가정**하고 처리합니다. 다른 컬럼을 기본키로 사용하는 테이블은 지원하지 않습니다.
* 로그는 `log/db_upsert.log` 경로에 저장되며, 실행 시 콘솔에도 출력됩니다.

## 테이블 구조
| 테이블          | 설명                                 |
| --------------- | ------------------------------------ |
| `regions`       | 대륙 수준 (아시아, 유럽 등)          |
| `subregions`    | 하위 지역 (동아시아, 동남아시아 등)  |
| `countries`     | 국가 정보 (이름, ISO, 한국어, 통화 등) |
| `states`        | 국가 내 도/주 (경기도, California 등) |
| `cities`        | 시/군/구 (서울, SF 등)               |

테이블 순서는 `regions` → `subregions` → `countries` → `states` → `cities`

## 데이터 출처
이 스크립트는 다음 공개 데이터베이스의 SQL dump를 기반으로 작성되어 있습니다:

* GitHub: [dr5hn/countries-states-cities-database](https://github.com/dr5hn/countries-states-cities-database)
* Dump 경로: [`psql/world.sql`](https://github.com/dr5hn/countries-states-cities-database/blob/master/psql/world.sql)
* ISO 3166 표준 기반의 국가/지역 데이터 포함
