"""
데이터베이스의 translation JSON 필드를 검증하는 스크립트

이 스크립트는 프로덕션 데이터베이스의 regions, subregions, countries 테이블에서
translations 컬럼의 JSON 유효성을 검사합니다.
"""

import psycopg2
import json
import sys
import logging
from pathlib import Path
from typing import List, Dict, Optional

# 프로젝트 루트를 sys.path에 추가
CURRENT_DIR = Path(__file__).resolve().parent
GEO_DB_ROOT = CURRENT_DIR.parent

if str(GEO_DB_ROOT) not in sys.path:
    sys.path.insert(0, str(GEO_DB_ROOT))

# common 모듈에서 필요한 것들 임포트
from common import (
    DatabaseValidationError,
    DatabaseManager,
    load_local_env,
    setup_logging,
    get_database_config,
    get_target_tables_from_env,
)


class DatabaseTranslationValidator(DatabaseManager):
    """데이터베이스의 translation JSON 필드를 검증하는 클래스"""

    def __init__(self, logger: Optional[logging.Logger] = None):
        """
        Args:
            logger: 로거 인스턴스 (옵션)
        """
        super().__init__(logger)

        # 데이터베이스 설정
        self.db_config = get_database_config("MAIN")

        # 검증 대상 테이블
        self.target_tables = get_target_tables_from_env(
            default_tables=["regions", "subregions", "countries"]
        )

        self.validation_errors: List[DatabaseValidationError] = []

    def _validate_table_translations(
        self, table_name: str
    ) -> List[DatabaseValidationError]:
        """특정 테이블의 translation 필드들을 검증합니다."""
        self.logger.info(f"테이블 '{table_name}' 데이터베이스 검증 시작")
        table_errors = []

        try:
            conn = self.get_connection(self.db_config)
            cur = conn.cursor()

            # 테이블에서 데이터 조회
            query = f"SELECT id, name, translations FROM public.{table_name}"
            cur.execute(query)
            rows = cur.fetchall()

            self.logger.info(f"테이블 '{table_name}'에서 {len(rows)}개의 행 검사")

            for row in rows:
                id_, name, translations = row
                try:
                    # JSON 유효성 검사
                    json.loads(translations)
                except (json.JSONDecodeError, TypeError) as e:
                    error = DatabaseValidationError(
                        table=table_name,
                        id=id_,
                        name=name,
                        error_message=str(e),
                        translations_content=translations,
                    )
                    table_errors.append(error)
                    self.logger.warning(
                        f"[{table_name}] [id:{id_}] [name:{name}]: JSON 오류 - {str(e)}"
                    )

            cur.close()
            conn.close()

            if not table_errors:
                self.logger.info(f"테이블 '{table_name}' 검증 완료: 오류 없음")
            else:
                self.logger.warning(
                    f"테이블 '{table_name}' 검증 완료: {len(table_errors)}개 오류 발견"
                )

        except psycopg2.Error as e:
            error_msg = f"테이블 '{table_name}' 접근 중 데이터베이스 오류: {e}"
            self.logger.error(error_msg)
            raise Exception(error_msg) from e
        except Exception as e:
            error_msg = f"테이블 '{table_name}' 검증 중 예상치 못한 오류: {e}"
            self.logger.error(error_msg)
            raise Exception(error_msg) from e

        return table_errors

    def validate_all_tables(self) -> bool:
        """모든 대상 테이블의 translation 필드를 검증합니다."""
        self.logger.info("=== 데이터베이스 Translation 검증 시작 ===")
        self.validation_errors.clear()

        for table in self.target_tables:
            table_errors = self._validate_table_translations(table)
            self.validation_errors.extend(table_errors)

        is_valid = len(self.validation_errors) == 0
        if is_valid:
            self.logger.info(
                "=== 데이터베이스 Translation 검증 완료: 모든 검증 통과 ==="
            )
        else:
            self.logger.error(
                f"=== 데이터베이스 Translation 검증 완료: {len(self.validation_errors)}개 오류 발견 ==="
            )

        return is_valid

    def validate_single_table(self, table_name: str) -> bool:
        """단일 테이블의 translation 필드를 검증합니다."""
        self.logger.info(f"=== 단일 테이블 '{table_name}' Translation 검증 시작 ===")

        try:
            table_errors = self._validate_table_translations(table_name)
            self.validation_errors = table_errors

            is_valid = len(table_errors) == 0
            if is_valid:
                self.logger.info(
                    f"=== 테이블 '{table_name}' Translation 검증 완료: 검증 통과 ==="
                )
            else:
                self.logger.error(
                    f"=== 테이블 '{table_name}' Translation 검증 완료: {len(table_errors)}개 오류 발견 ==="
                )

            return is_valid

        except Exception as e:
            self.logger.error(f"테이블 '{table_name}' 검증 중 오류: {e}")
            raise

    def print_results(self) -> None:
        """검증 결과를 출력합니다."""
        if self.validation_errors:
            print(
                f"\n🔍 총 {len(self.validation_errors)}개의 JSON 오류 행이 발견되었습니다:\n"
            )
            for error in self.validation_errors:
                print(f"테이블: {error.table}")
                print(f"ID: {error.id}, Name: {error.name}")
                print(f"Error: {error.error_message}")
                print(f"Translations: {error.translations_content[:100]}...")
                print("-" * 50)
        else:
            print("✅ 모든 translations 필드는 유효한 JSON입니다.")

    def get_error_count(self) -> int:
        """오류 개수를 반환합니다."""
        return len(self.validation_errors)

    def get_errors_by_table(self) -> Dict[str, List[DatabaseValidationError]]:
        """테이블별로 그룹화된 오류를 반환합니다."""
        errors_by_table = {}
        for error in self.validation_errors:
            if error.table not in errors_by_table:
                errors_by_table[error.table] = []
            errors_by_table[error.table].append(error)
        return errors_by_table


def main():
    """메인 실행 함수"""
    try:
        # 환경 변수 로드
        load_local_env()

        # 로깅 설정
        logger = setup_logging(
            base_path=GEO_DB_ROOT,
            log_dir_env="TRANSLATION_LOG_DIR",
            log_file_env="POST_CHECK_LOG_FILE",
        )

        # 명령행 인수로 특정 테이블 지정 가능
        if len(sys.argv) > 1:
            table_name = sys.argv[1]
            logger.info(f"단일 테이블 검증 모드: {table_name}")

            validator = DatabaseTranslationValidator(logger)
            is_valid = validator.validate_single_table(table_name)
        else:
            logger.info("전체 테이블 검증 모드")

            validator = DatabaseTranslationValidator(logger)
            is_valid = validator.validate_all_tables()

        validator.print_results()

        # 오류가 있으면 종료 코드 1로 종료
        sys.exit(0 if is_valid else 1)

    except Exception as e:
        if "logger" in locals():
            logger.error(f"오류 발생: {e}")
        print(f"오류 발생: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
