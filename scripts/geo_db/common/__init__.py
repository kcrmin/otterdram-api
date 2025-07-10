"""
Geo DB 공통 모듈

이 패키지는 geo_db 스크립트들에서 공통으로 사용되는
클래스, 함수, 유틸리티들을 제공합니다.
"""

from .models import (
    ValidationError,
    FixResult,
    DatabaseConfig,
    DatabaseValidationError,
)

from .utils import (
    load_local_env,
    get_database_config,
    validate_database_config,
    get_target_tables_from_env,
    setup_logging,
    create_backup_file,
    get_bool_env,
    get_int_env,
    get_list_env,
    format_json_error_context,
    parse_json_error_details,
    format_json_for_display,
    format_json_error_with_context,
    find_error_in_json_structure,
)

from .json_fixer import JSONFixer

from .database import DatabaseManager

__all__ = [
    # Models
    "ValidationError",
    "FixResult",
    "DatabaseConfig",
    "DatabaseValidationError",
    # Utils
    "load_local_env",
    "get_database_config",
    "validate_database_config",
    "get_target_tables_from_env",
    "setup_logging",
    "create_backup_file",
    "get_bool_env",
    "get_int_env",
    "get_list_env",
    "format_json_error_context",
    "parse_json_error_details",
    "format_json_for_display",
    "format_json_error_with_context",
    "find_error_in_json_structure",
    # JSON Fixer
    "JSONFixer",
    # Database
    "DatabaseManager",
]
