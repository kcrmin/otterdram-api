"""
공통 데이터 모델들
"""

from dataclasses import dataclass
from typing import List, Optional, Any


@dataclass
class ValidationError:
    """JSON 유효성 검사 오류 정보를 담는 데이터 클래스"""

    table: str
    row_id: str
    line_number: int
    error_context: str
    json_debug_info: str = ""  # 디버깅을 위한 전체 JSON 정보
    original_json: str = ""  # 원본 JSON 문자열
    corrected_json: str = ""  # 수정된 JSON 문자열


@dataclass
class FixResult:
    """수정 결과를 담는 데이터 클래스"""

    success: bool
    error_count_before: int
    error_count_after: int
    fixes_applied: List[str]
    backup_path: Optional[str] = None


@dataclass
class DatabaseConfig:
    """데이터베이스 연결 설정을 담는 데이터 클래스"""

    dbname: str
    user: str
    password: str
    host: str
    port: str


@dataclass
class DatabaseValidationError:
    """데이터베이스 JSON 유효성 검사 오류 정보를 담는 데이터 클래스"""

    table: str
    id: Any
    name: str
    error_message: str
    translations_content: str
