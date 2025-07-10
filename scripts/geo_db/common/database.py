"""
데이터베이스 관련 공통 기능들
"""

import psycopg2
import subprocess
import time
import logging
from typing import Optional, List
from pathlib import Path
from .models import DatabaseConfig


class DatabaseManager:
    """데이터베이스 작업을 위한 기본 매니저 클래스"""

    def __init__(self, logger: Optional[logging.Logger] = None):
        """
        Args:
            logger: 로거 인스턴스 (옵션)
        """
        self.logger = logger or logging.getLogger(__name__)

    def run_command(self, command: List[str], description: str) -> None:
        """명령어를 실행하고 결과를 로그에 기록합니다.

        Args:
            command: 실행할 명령어 리스트
            description: 명령어 설명

        Raises:
            Exception: 명령어 실행에 실패한 경우
        """
        try:
            self.logger.info(f"{description}: {' '.join(command)}")
            result = subprocess.run(command, check=True, capture_output=True, text=True)
            if result.stdout:
                self.logger.debug(f"명령어 출력: {result.stdout.strip()}")
        except subprocess.CalledProcessError as e:
            error_msg = (
                f"{description} 실패: {e.stderr.strip() if e.stderr else str(e)}"
            )
            self.logger.error(error_msg)
            raise Exception(error_msg) from e

    def wait_for_table(
        self, conn: psycopg2.extensions.connection, table_name: str, timeout: int = 10
    ) -> None:
        """테이블이 생성될 때까지 대기합니다.

        Args:
            conn: 데이터베이스 연결
            table_name: 대기할 테이블명
            timeout: 대기 시간 (초)

        Raises:
            Exception: 테이블이 시간 내에 생성되지 않은 경우
        """
        self.logger.info(f"⏳ {table_name} 테이블 생성 대기 중...")

        for attempt in range(timeout):
            try:
                with conn.cursor() as cur:
                    cur.execute(f"SELECT 1 FROM {table_name} LIMIT 1")
                    self.logger.info(f"✅ {table_name} 테이블 확인 완료")
                    return
            except psycopg2.errors.UndefinedTable:
                if attempt < timeout - 1:
                    time.sleep(1)
                else:
                    error_msg = (
                        f"{table_name} 테이블이 {timeout}초 내에 생성되지 않았습니다"
                    )
                    self.logger.error(error_msg)
                    raise Exception(error_msg)

    def create_database(self, config: DatabaseConfig) -> None:
        """데이터베이스를 생성합니다.

        Args:
            config: 데이터베이스 설정
        """
        self.run_command(
            ["createdb", config.dbname], f"데이터베이스 '{config.dbname}' 생성"
        )

    def drop_database(self, config: DatabaseConfig, if_exists: bool = True) -> None:
        """데이터베이스를 삭제합니다.

        Args:
            config: 데이터베이스 설정
            if_exists: 존재하지 않아도 에러를 발생시키지 않음
        """
        command = ["dropdb"]
        if if_exists:
            command.append("--if-exists")
        command.append(config.dbname)

        self.run_command(command, f"데이터베이스 '{config.dbname}' 삭제")

    def import_sql_file(self, config: DatabaseConfig, sql_file: Path) -> None:
        """SQL 파일을 데이터베이스에 임포트합니다.

        Args:
            config: 데이터베이스 설정
            sql_file: 임포트할 SQL 파일 경로
        """
        if not sql_file.exists():
            raise FileNotFoundError(f"SQL 파일이 존재하지 않습니다: {sql_file}")

        self.run_command(
            ["psql", "-d", config.dbname, "-f", str(sql_file)],
            f"SQL 파일 '{sql_file}' 임포트",
        )

    def get_connection(self, config: DatabaseConfig) -> psycopg2.extensions.connection:
        """데이터베이스 연결을 생성합니다.

        Args:
            config: 데이터베이스 설정

        Returns:
            데이터베이스 연결 객체
        """
        try:
            return psycopg2.connect(**config.__dict__)
        except psycopg2.Error as e:
            error_msg = f"데이터베이스 연결 실패: {e}"
            self.logger.error(error_msg)
            raise Exception(error_msg) from e

    def execute_upsert(
        self,
        conn: psycopg2.extensions.connection,
        table_name: str,
        rows: List[tuple],
        col_names: List[str],
        primary_key: str = "id",
    ) -> None:
        """테이블에 UPSERT를 실행합니다.

        Args:
            conn: 데이터베이스 연결
            table_name: 테이블명
            rows: 삽입할 데이터 행들
            col_names: 컬럼명들
            primary_key: 기본키 컬럼명
        """
        if not rows:
            self.logger.warning(f"⚠️ {table_name} 테이블에 삽입할 데이터가 없습니다")
            return

        # upsert 쿼리 생성
        placeholders = ",".join(["%s"] * len(col_names))
        columns = ",".join([f'"{col}"' for col in col_names])
        updates = ",".join(
            [f'"{col}"=EXCLUDED."{col}"' for col in col_names if col != primary_key]
        )

        insert_query = f"""
            INSERT INTO {table_name} ({columns})
            VALUES ({placeholders})
            ON CONFLICT ({primary_key}) DO UPDATE SET {updates}
        """

        with conn.cursor() as cur:
            cur.executemany(insert_query, rows)

        self.logger.info(f"✅ {table_name} 테이블 upsert 완료 ({len(rows)} rows)")
