"""
world.sql 기반 국가/지역 데이터 자동 반영 스크립트

이 스크립트는 world.sql 데이터 파일을 PostgreSQL 임시 DB에 로드한 후,
프로덕션 DB의 regions, subregions, countries, states, cities 테이블을
UPSERT 방식으로 안전하게 복사합니다.
"""

import psycopg2
import os
import sys
import logging
from typing import Optional
from pathlib import Path

# 프로젝트 루트를 sys.path에 추가
CURRENT_DIR = Path(__file__).resolve().parent
GEO_DB_ROOT = CURRENT_DIR.parent

if str(GEO_DB_ROOT) not in sys.path:
    sys.path.insert(0, str(GEO_DB_ROOT))

# common 모듈에서 필요한 것들 임포트
from common import (
    DatabaseManager,
    load_local_env,
    setup_logging,
    get_database_config,
    validate_database_config,
    get_target_tables_from_env,
    get_int_env,
)


class GeoDataUpsertManager(DatabaseManager):
    """지리 데이터 업서트 작업을 관리하는 클래스"""

    def __init__(self, logger: Optional[logging.Logger] = None):
        """
        Args:
            logger: 로거 인스턴스 (옵션)
        """
        super().__init__(logger)

        # 데이터베이스 설정
        self.main_db_config = get_database_config("MAIN")
        self.temp_db_config = get_database_config("TEMP")

        # Dump 파일 경로
        self.dump_file = Path(os.getenv("DUMP_FILE", ""))

        # 대상 테이블 목록
        self.target_tables = get_target_tables_from_env()

        # 대기 테이블과 타임아웃 설정
        self.wait_table = os.getenv("WAIT_TABLE", "subregions")
        self.wait_timeout = get_int_env("WAIT_TIMEOUT", 10)

    def _validate_config(self) -> None:
        """설정 유효성을 검사합니다."""
        if not self.dump_file.exists():
            error_msg = f"Dump 파일이 존재하지 않습니다: {self.dump_file}"
            self.logger.error(error_msg)
            raise FileNotFoundError(error_msg)

        validate_database_config(self.main_db_config, "MAIN")
        validate_database_config(self.temp_db_config, "TEMP")

    def _setup_temp_database(self) -> None:
        """임시 데이터베이스를 설정합니다."""
        self.logger.info("🚧 임시 DB 설정 시작")

        # 기존 임시 DB 삭제
        self.drop_database(self.temp_db_config, if_exists=True)

        # 새 임시 DB 생성
        self.create_database(self.temp_db_config)

        # dump 파일 import
        self.logger.info("📥 Dump 파일 import 시작")
        self.import_sql_file(self.temp_db_config, self.dump_file)

    def _upsert_table(
        self,
        table_name: str,
        temp_conn: psycopg2.extensions.connection,
        main_conn: psycopg2.extensions.connection,
        primary_key: str = "id",
    ) -> None:
        """특정 테이블을 upsert합니다."""
        self.logger.info(f"🔄 {table_name} 테이블 upsert 시작")

        # 임시 DB에서 데이터 조회
        with temp_conn.cursor() as cur:
            cur.execute(f"SELECT * FROM {table_name}")
            rows = cur.fetchall()
            col_names = [desc[0] for desc in cur.description]

        # UPSERT 실행
        self.execute_upsert(main_conn, table_name, rows, col_names, primary_key)

    def _cleanup_temp_database(self) -> None:
        """임시 데이터베이스를 정리합니다."""
        try:
            self.drop_database(self.temp_db_config)
            self.logger.info("🧹 임시 DB 제거 완료")
        except Exception as e:
            self.logger.warning(f"임시 DB 제거 중 오류 (무시 가능): {e}")

    def run_upsert_process(self) -> None:
        """전체 upsert 프로세스를 실행합니다."""
        self.logger.info("=== DB Upsert 프로세스 시작 ===")

        # 설정 검증
        self._validate_config()

        temp_conn = None
        main_conn = None

        try:
            # 임시 DB 설정
            self._setup_temp_database()

            # DB 연결
            self.logger.info("🔗 데이터베이스 연결 시작")
            temp_conn = self.get_connection(self.temp_db_config)
            main_conn = self.get_connection(self.main_db_config)

            # 테이블 대기
            self.wait_for_table(temp_conn, self.wait_table, self.wait_timeout)

            # 테이블별 upsert 실행
            with main_conn:
                for table in self.target_tables:
                    self._upsert_table(table, temp_conn, main_conn)

            self.logger.info("🎉 모든 테이블 업데이트 완료!")

        except Exception as e:
            err_msg = " ".join(str(e).splitlines())  # 줄바꿈 제거
            self.logger.error(f"🚨 에러 발생: {err_msg} ({type(e).__name__})")
            raise

        finally:
            # 연결 정리
            if temp_conn and not temp_conn.closed:
                temp_conn.close()
            if main_conn and not main_conn.closed:
                main_conn.close()
            self.logger.info("🔌 DB 연결 종료")

            # 임시 DB 정리
            self._cleanup_temp_database()

        self.logger.info("=== DB Upsert 프로세스 완료 ===")


def main() -> None:
    """메인 실행 함수"""
    try:
        # 환경 변수 로드
        load_local_env()

        # 로깅 설정
        logger = setup_logging(
            base_path=GEO_DB_ROOT,
            log_dir_env="DB_UPSERT_LOG_DIR",
            log_file_env="DB_UPSERT_LOG_FILE",
        )

        # DB 매니저 생성 및 실행
        db_manager = GeoDataUpsertManager(logger)
        db_manager.run_upsert_process()

        logger.info("=== 프로그램 정상 종료 ===")
        sys.exit(0)

    except Exception as e:
        if "logger" in locals():
            logger.error(f"메인 함수에서 오류 발생: {e}")
        print(f"오류 발생: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
