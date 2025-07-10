"""
공통 유틸리티 함수들
"""

import os
import logging
import shutil
import json
import re
from pathlib import Path
from typing import Optional
from dotenv import load_dotenv
from .models import DatabaseConfig


def load_local_env() -> None:
    """현재 파일이 위치한 디렉토리 기준으로 .env를 로드"""
    # 이 함수가 common 폴더에 있으므로 상위 디렉토리의 .env를 찾음
    env_path = Path(__file__).resolve().parent.parent / ".env"
    if not env_path.exists():
        raise FileNotFoundError(f"{env_path} 파일이 없습니다.")
    load_dotenv(dotenv_path=env_path)


def get_database_config(prefix: str = "MAIN") -> DatabaseConfig:
    """환경변수에서 데이터베이스 설정을 가져옵니다.

    Args:
        prefix: 환경변수 접두사 (MAIN, TEMP 등)

    Returns:
        DatabaseConfig 객체
    """
    return DatabaseConfig(
        dbname=os.getenv(f"{prefix}_DB_NAME", ""),
        user=os.getenv(f"{prefix}_DB_USER", ""),
        password=os.getenv(f"{prefix}_DB_PASSWORD", ""),
        host=os.getenv(f"{prefix}_DB_HOST", "localhost"),
        port=os.getenv(f"{prefix}_DB_PORT", "5432"),
    )


def validate_database_config(config: DatabaseConfig, prefix: str = "") -> None:
    """데이터베이스 설정의 유효성을 검사합니다.

    Args:
        config: 검증할 데이터베이스 설정
        prefix: 오류 메시지에 사용할 접두사

    Raises:
        ValueError: 필수 설정이 누락된 경우
    """
    if not config.dbname:
        raise ValueError(f"{prefix} DB 이름이 설정되지 않았습니다")
    if not config.user:
        raise ValueError(f"{prefix} DB 사용자가 설정되지 않았습니다")


def get_target_tables_from_env(default_tables: list = None) -> list:
    """환경변수에서 대상 테이블 목록을 가져옵니다.

    Args:
        default_tables: 기본 테이블 목록

    Returns:
        테이블 목록
    """
    if default_tables is None:
        default_tables = ["regions", "subregions", "countries", "states", "cities"]

    tables_env = os.getenv("TARGET_TABLES", ",".join(default_tables))
    return [table.strip() for table in tables_env.split(",") if table.strip()]


def setup_logging(
    base_path: Path, log_dir_env: str, log_file_env: str
) -> logging.Logger:
    """로깅 설정을 초기화합니다.

    Args:
        base_path: 기준 경로
        log_dir_env: 로그 디렉토리 환경변수명
        log_file_env: 로그 파일 환경변수명
    """
    log_dir = base_path / os.getenv(log_dir_env, "log")
    log_dir.mkdir(parents=True, exist_ok=True)

    log_file = log_dir / os.getenv(log_file_env, "app.log")

    logger = logging.getLogger("geo_db_logger")
    logger.setLevel(logging.INFO)

    # 기존 핸들러 제거
    if logger.hasHandlers():
        logger.handlers.clear()

    # 포매터 설정
    formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(message)s")

    # 파일 핸들러
    file_handler = logging.FileHandler(log_file, encoding="utf-8")
    file_handler.setLevel(logging.INFO)
    file_handler.setFormatter(formatter)
    logger.addHandler(file_handler)

    # 콘솔 핸들러
    console_handler = logging.StreamHandler()
    console_handler.setLevel(logging.INFO)
    console_handler.setFormatter(formatter)
    logger.addHandler(console_handler)

    return logger


def create_backup_file(file_path: Path, create_backup: bool = True) -> Optional[str]:
    """파일의 백업을 생성합니다.

    Args:
        file_path: 백업할 파일 경로
        create_backup: 백업 생성 여부

    Returns:
        백업 파일 경로 (생성하지 않으면 None)
    """
    if not create_backup:
        return None

    try:
        # 백업 파일명을 filename_backup.extension 형태로 생성
        # 예: world.sql -> world_backup.sql
        #     config.json -> config_backup.json
        #     filename -> filename_backup

        file_stem = file_path.stem  # 파일명 (확장자 제외)
        file_suffix = file_path.suffix  # 확장자 (.sql, .json 등)

        # _backup을 파일명에 추가
        backup_name = f"{file_stem}_backup{file_suffix}"
        backup_path = file_path.parent / backup_name

        # 기존 백업 파일이 있으면 덮어쓰기
        shutil.copy2(file_path, backup_path)
        return str(backup_path)
    except Exception as e:
        raise Exception(f"백업 파일 생성 실패: {e}")


def get_bool_env(env_name: str, default: bool = False) -> bool:
    """환경변수에서 boolean 값을 가져옵니다."""
    return os.getenv(env_name, str(default)).lower() == "true"


def get_int_env(env_name: str, default: int = 0) -> int:
    """환경변수에서 정수 값을 가져옵니다."""
    try:
        return int(os.getenv(env_name, str(default)))
    except ValueError:
        return default


def get_list_env(env_name: str, default: str = "", delimiter: str = ",") -> list:
    """환경변수에서 리스트 값을 가져옵니다."""
    value = os.getenv(env_name, default)
    return [item.strip() for item in value.split(delimiter) if item.strip()]


def format_json_error_context(
    json_str: str, error_pos: int, context_lines: int = 3
) -> str:
    """JSON 오류 위치 주변의 컨텍스트를 간결하게 포맷팅합니다.

    Args:
        json_str: 원본 JSON 문자열
        error_pos: 오류 발생 위치
        context_lines: 앞뒤로 보여줄 라인 수

    Returns:
        포맷팅된 오류 컨텍스트
    """
    # 원본 JSON을 라인별로 분할
    lines = json_str.split("\n")

    # 오류 위치에서 라인 번호와 컬럼 번호 찾기
    current_pos = 0
    error_line_num = 0
    error_col_num = 0

    for line_num, line in enumerate(lines):
        line_end_pos = current_pos + len(line)
        if error_pos <= line_end_pos:
            error_line_num = line_num
            error_col_num = error_pos - current_pos
            break
        current_pos = line_end_pos + 1  # +1 for newline character

    # 컨텍스트 범위 계산
    start_line = max(0, error_line_num - context_lines)
    end_line = min(len(lines), error_line_num + context_lines + 1)

    # 결과 생성
    result_lines = []

    # 앞부분이 생략되었으면 ... 추가
    if start_line > 0:
        result_lines.append("                    ...")

    for i in range(start_line, end_line):
        line = lines[i].strip()
        if i == error_line_num:
            # 오류 라인에 [error] 마커 추가하고 오류 위치 표시
            if error_col_num < len(lines[i]):
                # 오류 위치 앞과 뒤를 분리
                before_error = lines[i][:error_col_num].strip()
                error_char = (
                    lines[i][error_col_num] if error_col_num < len(lines[i]) else ""
                )
                after_error = lines[i][error_col_num + 1 :].strip()

                # 오류 문자가 있으면 강조
                if error_char:
                    result_lines.append(
                        f"[error]         {before_error}[HERE->'{error_char}']<-{after_error}"
                    )
                else:
                    result_lines.append(f"[error]         {line}")
            else:
                result_lines.append(f"[error]         {line}")
        else:
            result_lines.append(f"                {line}")

    # 뒷부분이 생략되었으면 ... 추가
    if end_line < len(lines):
        result_lines.append("                    ...")

    return "\n".join(result_lines)


def parse_json_error_details(
    json_str: str, error: json.JSONDecodeError
) -> tuple[str, str]:
    """JSON 오류의 상세 정보를 파싱합니다.

    Args:
        json_str: 원본 JSON 문자열
        error: JSONDecodeError 객체

    Returns:
        (오류_타입, 오류_메시지) 튜플
    """
    error_type = type(error).__name__
    error_msg = str(error).split(":")[0] if ":" in str(error) else str(error)

    # 오류 위치의 실제 문자 찾기
    if hasattr(error, "pos") and error.pos < len(json_str):
        error_char = json_str[error.pos]
        if error_char in ['"', "'", ":", ",", "{", "}", "[", "]"]:
            error_msg += f" (문자: '{error_char}')"

    return error_type, error_msg


def format_json_for_display(json_str: str, max_lines: int = 10) -> str:
    """JSON을 보기 좋게 포맷팅합니다.

    Args:
        json_str: 원본 JSON 문자열
        max_lines: 최대 표시할 라인 수

    Returns:
        포맷팅된 JSON 문자열
    """
    try:
        # JSON 파싱하고 예쁘게 포맷팅
        parsed = json.loads(json_str)
        formatted = json.dumps(parsed, ensure_ascii=False, indent=4)
        lines = formatted.split("\n")

        # 라인 수가 많으면 줄여서 표시
        if len(lines) > max_lines:
            half_lines = max_lines // 2
            result_lines = []
            result_lines.extend(lines[:half_lines])
            result_lines.append("                    ...")
            result_lines.extend(lines[-half_lines:])
            return "\n".join([f"                {line}" for line in result_lines])
        else:
            return "\n".join([f"                {line}" for line in lines])

    except (json.JSONDecodeError, ValueError, TypeError):
        # 파싱 실패시 원본 반환
        lines = json_str.split("\n")
        if len(lines) > max_lines:
            half_lines = max_lines // 2
            result_lines = []
            result_lines.extend(lines[:half_lines])
            result_lines.append("                    ...")
            result_lines.extend(lines[-half_lines:])
            return "\n".join(
                [f"                {line.strip()}" for line in result_lines]
            )
        else:
            return "\n".join([f"                {line.strip()}" for line in lines])


def format_json_error_with_context(json_str: str, error_pos: int) -> str:
    """JSON 오류 위치를 정확하게 찾아서 앞뒤 정상 부분과 함께 표시합니다.

    Args:
        json_str: 원본 JSON 문자열
        error_pos: 오류 발생 위치

    Returns:
        포맷팅된 오류 컨텍스트
    """
    import re

    try:
        # JSON을 한 줄로 만들어서 파싱 시도
        clean_json = re.sub(r"\s+", " ", json_str.strip())

        # key-value 패턴 찾기 (정규식으로 key:value 쌍들 추출)
        kv_pattern = r'"([^"]+)"\s*:\s*("[^"]*"|[^,}]+)'
        matches = list(re.finditer(kv_pattern, clean_json))

        result_lines = []
        result_lines.append("                {")

        if matches:
            # 오류 위치가 어느 매치 근처에 있는지 찾기
            error_match_idx = -1
            for i, match in enumerate(matches):
                if match.start() <= error_pos <= match.end():
                    error_match_idx = i
                    break

            # 앞에서부터 정상적인 key-value 쌍들 표시
            start_idx = (
                max(0, error_match_idx - 1)
                if error_match_idx >= 0
                else max(0, len(matches) - 2)
            )
            end_idx = (
                min(len(matches), error_match_idx + 2)
                if error_match_idx >= 0
                else len(matches)
            )

            # 앞부분 생략 표시
            if start_idx > 0:
                result_lines.append("                    ...")

            for i in range(start_idx, end_idx):
                if i < len(matches):
                    match = matches[i]
                    key = match.group(1)
                    value = match.group(2)

                    # 오류가 있는 매치인지 확인
                    if i == error_match_idx:
                        # 오류 위치가 이 매치 안에 있음
                        if error_pos - match.start() < len(key) + 3:  # key 부분에 오류
                            result_lines.append(f'[error]         "{key}": {value},')
                        else:  # value 부분에 오류
                            result_lines.append(f'[error]         "{key}": {value},')
                    else:
                        # 정상적인 key-value 쌍
                        comma = "," if i < len(matches) - 1 else ""
                        result_lines.append(
                            f'                    "{key}": {value}{comma}'
                        )

            # 뒷부분 생략 표시
            if end_idx < len(matches):
                result_lines.append("                    ...")
        else:
            # 매치가 없으면 원본 그대로 오류 표시
            result_lines.append(f"[error]         {json_str.strip()}")

        result_lines.append("                }")
        return "\n".join(result_lines)

    except Exception:
        # 파싱 실패시 기본 포맷 사용
        return format_json_error_context(json_str, error_pos, 2)


def find_error_in_json_structure(json_str: str, error_pos: int) -> str:
    """JSON 구조에서 오류 위치를 분석하여 컨텍스트를 생성합니다."""

    # JSON을 한 줄로 정리
    clean_json = re.sub(r"\s+", " ", json_str.strip())

    # 이미 중괄호로 감싸져 있다면 제거
    if clean_json.startswith("{") and clean_json.endswith("}"):
        clean_json = clean_json[1:-1].strip()

    # 키-값 쌍을 찾는 정규식 (더 정확한 패턴)
    kv_pattern = r'"([^"]+)"\s*:\s*"([^"]*)"'
    matches = list(re.finditer(kv_pattern, clean_json))

    if not matches:
        return "{\n[error]     잘못된 JSON 형식\n}"

    # 오류 위치를 기준으로 앞뒤 정상 키-값 쌍 찾기
    last_valid_before = None
    first_valid_after = None

    # 앞에서부터: 오류 위치 이전의 마지막 정상 키-값 쌍
    for match in matches:
        if match.end() <= error_pos:
            last_valid_before = (match.group(1), match.group(2))
        else:
            break

    # 뒤에서부터: 오류 위치 이후의 첫 번째 정상 키-값 쌍
    for match in matches:
        if match.start() >= error_pos:
            first_valid_after = (match.group(1), match.group(2))
            break

    # 오류 부분 추출
    error_start = 0
    error_end = len(clean_json)

    if last_valid_before:
        # 마지막 정상 키-값 쌍 이후부터 오류 시작
        for match in matches:
            if (
                match.group(1) == last_valid_before[0]
                and match.group(2) == last_valid_before[1]
            ):
                error_start = match.end()
                break

    if first_valid_after:
        # 첫 번째 정상 키-값 쌍 이전까지 오류
        for match in matches:
            if (
                match.group(1) == first_valid_after[0]
                and match.group(2) == first_valid_after[1]
            ):
                error_end = match.start()
                break

    # 오류 부분 정리
    error_part = clean_json[error_start:error_end].strip()
    # 앞뒤 콤마 제거
    error_part = error_part.strip(",").strip()

    # 결과 구성 (간결한 들여쓰기)
    result_lines = ["{"]

    # 앞에 정상 키-값이 있고, 첫 번째 요소가 아닌 경우에만 ... 표시
    if last_valid_before and matches[0].group(1) != last_valid_before[0]:
        result_lines.append("    ...")

    if last_valid_before:
        result_lines.append(f'    "{last_valid_before[0]}": "{last_valid_before[1]}",')

    if error_part:
        result_lines.append(f"[error] {error_part}")

    if first_valid_after:
        # first_valid_after가 마지막 요소가 아니면 콤마 추가
        is_last_element = (
            len(matches) > 0
            and first_valid_after[0] == matches[-1].group(1)
            and first_valid_after[1] == matches[-1].group(2)
        )
        comma = "" if is_last_element else ","
        result_lines.append(
            f'    "{first_valid_after[0]}": "{first_valid_after[1]}"{comma}'
        )

    # 뒤에 정상 키-값이 있고, 마지막 요소가 아닌 경우에만 ... 표시
    if first_valid_after and matches[-1].group(1) != first_valid_after[0]:
        result_lines.append("    ...")

    result_lines.append("}")

    return "\n".join(result_lines)
