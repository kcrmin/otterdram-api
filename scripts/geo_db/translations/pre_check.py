"""
SQL 파일의 translation JSON 필드를 검증하는 스크립트

이 스크립트는 world.sql 파일의 INSERT 문에서 translations 컬럼의 JSON 유효성을 검사하고,
선택적으로 자동 수정을 수행합니다.
"""

import re
import json
import sys
import os
import logging
from pathlib import Path
from typing import List, Tuple, Optional

# 프로젝트 루트를 sys.path에 추가
CURRENT_DIR = Path(__file__).resolve().parent
GEO_DB_ROOT = CURRENT_DIR.parent

if str(GEO_DB_ROOT) not in sys.path:
    sys.path.insert(0, str(GEO_DB_ROOT))

# common 모듈에서 필요한 것들 임포트
from common import (
    ValidationError,
    JSONFixer,
    load_local_env,
    setup_logging,
    parse_json_error_details,
    find_error_in_json_structure,
    get_bool_env,
    get_int_env,
    get_target_tables_from_env,
    create_backup_file,
)


class SQLTranslationValidator:
    """SQL 파일의 translation JSON 필드를 검증하는 클래스"""

    def __init__(self, sql_file_path: str, logger: Optional[logging.Logger] = None):
        """
        Args:
            sql_file_path: 검증할 SQL 파일 경로
            logger: 로거 인스턴스 (옵션)
        """
        # 환경 변수에서 설정 로드
        self.context_length = get_int_env("ERROR_CONTEXT_LENGTH", 30)
        self.target_tables = get_target_tables_from_env(
            default_tables=["regions", "subregions", "countries"]
        )

        # 상수 정의
        self.JSON_PATTERN = r"(\{.*?\})"
        self.SUCCESS_MESSAGE = "✅ 모든 translations 필드가 올바른 JSON 형식입니다."

        self.sql_file_path = Path(sql_file_path)
        self.sql_content = ""
        self.validation_errors: List[ValidationError] = []
        self.logger = logger or logging.getLogger("translation_validator")

        # JSON 수정기 초기화
        self.json_fixer = JSONFixer(self.logger)

    def _load_sql_file(self) -> None:
        """SQL 파일을 로드합니다."""
        try:
            self.logger.info(f"SQL 파일 로드 시작: {self.sql_file_path}")
            with open(self.sql_file_path, "r", encoding="utf-8") as f:
                self.sql_content = f.read()
            self.logger.info(f"SQL 파일 로드 완료: {len(self.sql_content)} 문자")
        except FileNotFoundError:
            error_msg = f"SQL 파일을 찾을 수 없습니다: {self.sql_file_path}"
            self.logger.error(error_msg)
            raise FileNotFoundError(error_msg)
        except Exception as e:
            error_msg = f"SQL 파일 읽기 중 오류 발생: {e}"
            self.logger.error(error_msg)
            raise Exception(error_msg)

    def _extract_insert_statements(self, table_name: str) -> List[Tuple[str, str, int]]:
        """특정 테이블의 INSERT 문을 추출합니다."""
        pattern = rf"(INSERT INTO public\.{table_name} VALUES\s*\((.*?)\);)"
        return [
            (match.group(1), match.group(2), match.start(1))
            for match in re.finditer(pattern, self.sql_content, re.DOTALL)
        ]

    def _extract_json_candidates(self, row_str: str) -> List[Tuple[int, str]]:
        """행 문자열에서 JSON 후보들을 추출합니다."""
        return [
            (match.start(), match.group())
            for match in re.finditer(self.JSON_PATTERN, row_str)
        ]

    def _get_error_context(self, json_str: str, error_pos: int) -> str:
        """JSON 오류 위치 주변의 컨텍스트를 반환합니다."""
        return find_error_in_json_structure(json_str, error_pos)

    def _find_line_number_from_offset(self, offset: int) -> int:
        """오프셋 위치의 라인 번호를 찾습니다."""
        return self.sql_content.count("\n", 0, offset) + 1

    def _validate_json_string(self, json_str: str) -> Optional[str]:
        """JSON 문자열의 유효성을 검사하고 오류 컨텍스트를 반환합니다."""
        try:
            json.loads(json_str)
            return None
        except json.JSONDecodeError as e:
            # 더 명확한 오류 메시지 생성
            error_type, error_msg = parse_json_error_details(json_str, e)
            formatted_json = self._get_error_context(json_str, e.pos)

            return f"{error_type}: {error_msg}\n{formatted_json}"

    def _parse_row_id(self, row_values_str: str) -> str:
        """행 값 문자열에서 ID를 파싱합니다."""
        parts = row_values_str.split(",", 1)
        return parts[0].strip() if parts else "?"

    def _validate_table_translations(self, table_name: str) -> None:
        """특정 테이블의 translation 필드들을 검증합니다."""
        self.logger.info(f"테이블 '{table_name}' 검증 시작")
        inserts = self._extract_insert_statements(table_name)
        self.logger.info(f"테이블 '{table_name}'에서 {len(inserts)}개의 INSERT 문 발견")

        table_errors = 0
        for full_stmt, values_str, offset in inserts:
            row_id = self._parse_row_id(values_str)
            line_num = self._find_line_number_from_offset(offset)

            json_candidates = self._extract_json_candidates(values_str)
            for _, json_str in json_candidates:
                error_context = self._validate_json_string(json_str)
                if error_context:
                    # 에러 컨텍스트에서 [error] 라인만 추출하여 로그에 포함
                    error_line = ""
                    if "\n" in error_context:
                        lines = error_context.split("\n")
                        for line in lines:
                            if "[error]" in line:
                                # [error] 부분만 추출
                                error_line = line.strip()
                                break

                    if not error_line:
                        error_line = "[error] JSON 파싱 오류"

                    error = ValidationError(
                        table=table_name,
                        row_id=row_id,
                        line_number=line_num,
                        error_context=error_context,
                        original_json=json_str,  # 원본 JSON 추가
                        json_debug_info=f"테이블: {table_name}, 행 ID: {row_id}, 라인: {line_num}",
                    )
                    self.validation_errors.append(error)
                    table_errors += 1
                    self.logger.warning(
                        f"테이블 '{table_name}' | ID: {row_id} | 라인 {line_num} | {error_line}"
                    )

        if table_errors == 0:
            self.logger.info(f"테이블 '{table_name}' 검증 완료: 오류 없음")
        else:
            self.logger.warning(
                f"테이블 '{table_name}' 검증 완료: {table_errors}개 오류 발견"
            )

    def validate(self) -> bool:
        """모든 테이블의 translation 필드를 검증합니다."""
        self.logger.info("=== Translation 검증 시작 ===")
        self._load_sql_file()
        self.validation_errors.clear()

        for table in self.target_tables:
            self._validate_table_translations(table)

        is_valid = len(self.validation_errors) == 0
        if is_valid:
            self.logger.info("=== Translation 검증 완료: 모든 검증 통과 ===")
        else:
            self.logger.error(
                f"=== Translation 검증 완료: {len(self.validation_errors)}개 오류 발견 ==="
            )

        return is_valid

    def print_results(self) -> None:
        """검증 결과를 출력합니다."""
        if self.validation_errors:
            print("\n" + "=" * 60)
            print("🔍 SQL 파일 JSON 검증 결과")
            print("=" * 60)

            # 테이블별로 그룹화하여 출력
            errors_by_table = {}
            for error in self.validation_errors:
                if error.table not in errors_by_table:
                    errors_by_table[error.table] = []
                errors_by_table[error.table].append(error)

            for table_name, table_errors in errors_by_table.items():
                print(f"\n📋 테이블: {table_name} ({len(table_errors)}개 오류)")
                print("-" * 40)

                for i, error in enumerate(table_errors, 1):
                    print(f"  {i}. ID: {error.row_id}")
                    print(f"     라인: {error.line_number}")

                    # 오류 메시지를 구조화하여 출력
                    if "\n" in error.error_context:
                        error_lines = error.error_context.split("\n", 1)
                        print(f"     오류 유형: {error_lines[0]}")
                        print("     JSON:")
                        # JSON 부분을 들여쓰기해서 출력
                        json_content = error_lines[1]
                        for line in json_content.split("\n"):
                            if line.strip():
                                # [error] 라인은 다른 들여쓰기 적용
                                if line.strip().startswith("[error]"):
                                    print(f"[error]       {line.strip()[7:].strip()}")
                                elif line.strip() in ["{", "}"]:
                                    # 중괄호는 가장 적게 들여쓰기
                                    print(f"           {line.strip()}")
                                else:
                                    # 일반 내용은 한 칸 더 들여쓰기
                                    print(f"              {line.strip()}")
                    else:
                        print(f"     오류: {error.error_context}")

                    if i < len(table_errors):
                        print()

            print("\n" + "=" * 60)
            print(
                f"📊 총 {len(self.validation_errors)}개의 JSON 오류가 발견되었습니다."
            )

            # 자동 수정 안내 메시지 추가
            auto_fix = get_bool_env("AUTO_FIX_ERRORS", False)
            if not auto_fix:
                print(
                    "💡 .env에서 AUTO_FIX_ERRORS=true로 설정하면 자동 수정을 시도합니다."
                )

            print("=" * 60)
        else:
            print("\n" + "=" * 50)
            print("✅ 모든 translations 필드가 올바른 JSON 형식입니다.")
            print("=" * 50)

    def get_error_count(self) -> int:
        """오류 개수를 반환합니다."""
        return len(self.validation_errors)

    def fix_json_errors(self) -> bool:
        """JSON 오류들을 자동으로 수정합니다."""
        if not self.validation_errors:
            self.logger.info("수정할 JSON 오류가 없습니다.")
            return True

        # 환경 변수에서 백업 설정 읽기
        create_backup = get_bool_env("CREATE_BACKUP", True)

        self.logger.info(
            f"JSON 오류 자동 수정 시작: {len(self.validation_errors)}개 오류"
        )

        # 백업 파일 생성
        backup_path = None
        if create_backup:
            try:
                backup_path = create_backup_file(self.sql_file_path, create_backup)
                if backup_path:
                    self.logger.info(f"백업 파일 생성: {backup_path}")
            except Exception as e:
                self.logger.error(f"백업 파일 생성 실패: {e}")
                return False

        # SQL 내용을 수정
        modified_content = self.sql_content
        fixes_applied = 0

        # 오류를 역순으로 처리 (뒤에서부터 수정해야 위치가 안 바뀜)
        sorted_errors = sorted(
            self.validation_errors, key=lambda x: x.line_number, reverse=True
        )

        for error in sorted_errors:
            try:
                # JSON 수정기를 사용하여 자동 수정 시도
                original_json = error.original_json
                if original_json:
                    fixed_json = self.json_fixer.fix_json_error(original_json)

                    if fixed_json and fixed_json != original_json:
                        # SQL 내용에서 원본 JSON을 수정된 JSON으로 교체
                        if original_json in modified_content:
                            modified_content = modified_content.replace(
                                original_json, fixed_json, 1
                            )
                            fixes_applied += 1
                            self.logger.info(
                                f"테이블 {error.table}, ID {error.row_id}: JSON 수정 완료"
                            )
                        else:
                            self.logger.warning(
                                f"테이블 {error.table}, ID {error.row_id}: 원본 JSON을 찾을 수 없음"
                            )
                    else:
                        # 자동 수정이 실패하면 빈 JSON으로 교체
                        fixed_json = "{}"
                        if original_json in modified_content:
                            modified_content = modified_content.replace(
                                original_json, fixed_json, 1
                            )
                            fixes_applied += 1
                            self.logger.info(
                                f"테이블 {error.table}, ID {error.row_id}: 빈 JSON으로 교체"
                            )

            except Exception as e:
                self.logger.error(
                    f"테이블 {error.table}, ID {error.row_id} 수정 실패: {e}"
                )

        # 수정된 내용을 파일에 저장
        if fixes_applied > 0:
            try:
                with open(self.sql_file_path, "w", encoding="utf-8") as f:
                    f.write(modified_content)

                self.logger.info(f"SQL 파일 수정 완료: {fixes_applied}개 오류 수정됨")

                # 수정 후 다시 검증
                self.sql_content = modified_content
                self.validation_errors.clear()
                self.logger.info("수정 후 재검증 시작...")

                for table in self.target_tables:
                    self._validate_table_translations(table)

                remaining_errors = len(self.validation_errors)
                if remaining_errors == 0:
                    self.logger.info("✅ 모든 JSON 오류가 수정되었습니다!")
                else:
                    self.logger.warning(
                        f"⚠️  {remaining_errors}개 오류가 여전히 남아있습니다."
                    )

                return remaining_errors == 0

            except Exception as e:
                self.logger.error(f"수정된 파일 저장 실패: {e}")
                return False
        else:
            self.logger.warning("수정된 오류가 없습니다.")
            return False


def main():
    """메인 실행 함수"""
    try:
        # 환경 변수 로드
        load_local_env()

        # 로깅 설정
        logger = setup_logging(
            base_path=GEO_DB_ROOT,
            log_dir_env="TRANSLATION_LOG_DIR",
            log_file_env="TRANSLATION_LOG_FILE",
        )

        # SQL 파일 경로 결정
        default_sql_path = os.getenv("DUMP_FILE")
        if not default_sql_path:
            # .env에 DUMP_FILE이 없는 경우 기본값 사용
            default_sql_path = str(GEO_DB_ROOT / "data" / "world.sql")
            logger.warning(
                f".env에 DUMP_FILE이 설정되지 않음. 기본값 사용: {default_sql_path}"
            )

        # 명령행 인수가 있으면 사용, 없으면 .env 파일의 DUMP_FILE 사용
        sql_path = sys.argv[1] if len(sys.argv) > 1 else default_sql_path

        logger.info(f"검증 대상 SQL 파일: {sql_path}")

        validator = SQLTranslationValidator(sql_path, logger)
        is_valid = validator.validate()

        # 오류가 있고 자동 수정이 활성화된 경우 수정 시도
        if not is_valid:
            auto_fix = get_bool_env("AUTO_FIX_ERRORS", False)
            if auto_fix:
                logger.info(
                    "🔧 자동 수정 모드가 활성화되어 있습니다. JSON 오류 수정을 시도합니다..."
                )
                fix_success = validator.fix_json_errors()
                if fix_success:
                    logger.info("✅ 모든 JSON 오류가 성공적으로 수정되었습니다!")
                    is_valid = True
                else:
                    logger.warning("⚠️  일부 오류가 수정되지 않았습니다.")
            else:
                logger.info(
                    "자동 수정 모드가 비활성화되어 있습니다. (.env에서 AUTO_FIX_ERRORS=true로 설정하면 활성화됩니다)"
                )

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
