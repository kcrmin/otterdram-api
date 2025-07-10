"""
프로젝트 import 관련 유틸리티
"""

import sys
from pathlib import Path
from typing import Any, Dict


def setup_project_imports(current_file: str) -> Dict[str, Any]:
    """
    프로젝트의 common 모듈을 import하고 필요한 모듈들을 반환합니다.

    Args:
        current_file: __file__ 변수 값

    Returns:
        common 모듈의 함수/클래스들을 담은 딕셔너리
    """
    # 프로젝트 루트 경로 계산
    current_dir = Path(current_file).resolve().parent
    geo_db_root = current_dir.parent

    # sys.path에 추가 (이미 있으면 무시)
    if str(geo_db_root) not in sys.path:
        sys.path.insert(0, str(geo_db_root))

    # common 모듈 import
    try:
        from common import (
            ValidationError,
            FixResult,
            DatabaseConfig,
            DatabaseValidationError,
            DatabaseManager,
            JSONFixer,
            load_local_env,
            setup_logging,
            get_database_config,
            validate_database_config,
            get_target_tables_from_env,
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

        return {
            "ValidationError": ValidationError,
            "FixResult": FixResult,
            "DatabaseConfig": DatabaseConfig,
            "DatabaseValidationError": DatabaseValidationError,
            "DatabaseManager": DatabaseManager,
            "JSONFixer": JSONFixer,
            "load_local_env": load_local_env,
            "setup_logging": setup_logging,
            "get_database_config": get_database_config,
            "validate_database_config": validate_database_config,
            "get_target_tables_from_env": get_target_tables_from_env,
            "create_backup_file": create_backup_file,
            "get_bool_env": get_bool_env,
            "get_int_env": get_int_env,
            "get_list_env": get_list_env,
            "format_json_error_context": format_json_error_context,
            "parse_json_error_details": parse_json_error_details,
            "format_json_for_display": format_json_for_display,
            "format_json_error_with_context": format_json_error_with_context,
            "find_error_in_json_structure": find_error_in_json_structure,
            "GEO_DB_ROOT": geo_db_root,
        }

    except ImportError as e:
        raise ImportError(f"common 모듈을 import할 수 없습니다: {e}")


def get_specific_imports(current_file: str, import_list: list) -> Dict[str, Any]:
    """
    특정 모듈들만 import해서 반환합니다.

    Args:
        current_file: __file__ 변수 값
        import_list: import할 모듈명들의 리스트

    Returns:
        요청된 모듈들을 담은 딕셔너리
    """
    all_imports = setup_project_imports(current_file)

    result = {}
    for item in import_list:
        if item in all_imports:
            result[item] = all_imports[item]
        else:
            raise ImportError(f"'{item}' 모듈을 찾을 수 없습니다")

    return result
