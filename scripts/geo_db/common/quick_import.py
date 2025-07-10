"""
프로젝트 import를 간단하게 만드는 헬퍼
"""

import sys
from pathlib import Path


class ProjectImporter:
    """프로젝트 import를 간단하게 만드는 클래스"""

    def __init__(self, current_file: str):
        """
        Args:
            current_file: __file__ 변수 값
        """
        self.current_file = current_file
        self._setup_path()
        self._import_common()

    def _setup_path(self):
        """프로젝트 경로를 sys.path에 추가"""
        current_dir = Path(self.current_file).resolve().parent
        geo_db_root = current_dir.parent

        if str(geo_db_root) not in sys.path:
            sys.path.insert(0, str(geo_db_root))

        self.GEO_DB_ROOT = geo_db_root

    def _import_common(self):
        """common 모듈의 모든 요소를 import"""
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

        # 속성으로 할당
        self.ValidationError = ValidationError
        self.FixResult = FixResult
        self.DatabaseConfig = DatabaseConfig
        self.DatabaseValidationError = DatabaseValidationError
        self.DatabaseManager = DatabaseManager
        self.JSONFixer = JSONFixer
        self.load_local_env = load_local_env
        self.setup_logging = setup_logging
        self.get_database_config = get_database_config
        self.validate_database_config = validate_database_config
        self.get_target_tables_from_env = get_target_tables_from_env
        self.create_backup_file = create_backup_file
        self.get_bool_env = get_bool_env
        self.get_int_env = get_int_env
        self.get_list_env = get_list_env
        self.format_json_error_context = format_json_error_context
        self.parse_json_error_details = parse_json_error_details
        self.format_json_for_display = format_json_for_display
        self.format_json_error_with_context = format_json_error_with_context
        self.find_error_in_json_structure = find_error_in_json_structure


def quick_import(current_file: str) -> ProjectImporter:
    """
    프로젝트의 모든 common 모듈을 한 번에 import하는 함수

    사용법:
        imp = quick_import(__file__)
        logger = imp.setup_logging(...)
        config = imp.get_database_config("MAIN")

    Args:
        current_file: __file__ 변수 값

    Returns:
        모든 common 모듈 요소를 가진 ProjectImporter 객체
    """
    return ProjectImporter(current_file)
