# 🌍 Geo DB Management Scripts

지리 데이터베이스 관리를 위한 Python 스크립트 모음입니다.

## 📋 스크립트 목록

### 1. `pre_check.py` - SQL 파일 검증
- `world.sql` 파일의 JSON translation 필드 유효성 검사
- 자동 오류 수정 기능 (옵션)

### 2. `post_check.py` - 데이터베이스 검증  
- 프로덕션 DB의 JSON translation 필드 검증
- 테이블별 상세 오류 리포트

### 3. `db_upsert.py` - 데이터 업서트
- `world.sql` 데이터를 프로덕션 DB에 안전하게 반영
- 임시 DB를 통한 UPSERT 방식

## 🚀 사용법

1. **환경 설정**
   ```bash
   cp .env.example .env
   # .env 파일을 실제 환경에 맞게 수정
   ```

2. **스크립트 실행**
   ```bash
   python pre_check.py     # SQL 파일 검증
   python post_check.py    # DB 검증
   python db_upsert.py     # 데이터 업서트
   ```

## 📊 지원 테이블

| 테이블 | 설명 |
|--------|------|
| `regions` | 대륙 (아시아, 유럽 등) |
| `subregions` | 하위 지역 (동아시아, 서유럽 등) |
| `countries` | 국가 (대한민국, 일본 등) |
| `states` | 주/도 (서울특별시, 경기도 등) |
| `cities` | 도시 (서울, 부산, 인천 등) |

## ⚠️ 주의사항

- `world.sql` 파일은 수정하지 마세요
- 임시 DB는 자동으로 생성/삭제됩니다
- UPSERT는 `id` 컬럼을 기본키로 가정합니다

## 📖 데이터 출처

[dr5hn/countries-states-cities-database](https://github.com/dr5hn/countries-states-cities-database)의 `world.sql` 기반