import psycopg2
import subprocess
import os
import logging
import time
from dotenv import load_dotenv
from pathlib import Path


def load_local_env():
    """현재 파일이 위치한 디렉토리 기준으로 .env를 로드"""
    env_path = Path(__file__).resolve().parent / ".env"
    if not env_path.exists():
        raise FileNotFoundError(f"{env_path} 파일이 없습니다.")
    load_dotenv(dotenv_path=env_path)


def setup_logging(base_path):
    log_dir = os.path.join(base_path, "log")
    os.makedirs(log_dir, exist_ok=True)

    log_file = os.path.join(log_dir, "db_upsert.log")

    logger = logging.getLogger(__name__)
    logger.setLevel(logging.INFO)

    if logger.hasHandlers():
        logger.handlers.clear()

    formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(message)s")

    file_handler = logging.FileHandler(log_file, encoding="utf-8")
    file_handler.setFormatter(formatter)

    stream_handler = logging.StreamHandler()
    stream_handler.setFormatter(formatter)

    logger.addHandler(file_handler)
    logger.addHandler(stream_handler)

    logger.info("=== 실행 시작 ===")
    return logger


def get_db_config(prefix="MAIN"):
    return {
        "dbname": os.getenv(f"{prefix}_DB_NAME"),
        "user": os.getenv(f"{prefix}_DB_USER"),
        "password": os.getenv(f"{prefix}_DB_PASSWORD"),
        "host": os.getenv(f"{prefix}_DB_HOST"),
        "port": os.getenv(f"{prefix}_DB_PORT", "5432"),
    }


def wait_for_table(conn, table_name, timeout=10, logger=None):
    if logger:
        logger.info(f"⏳ {table_name} 테이블 생성 대기 중...")
    for _ in range(timeout):
        with conn.cursor() as cur:
            try:
                cur.execute(f"SELECT 1 FROM {table_name} LIMIT 1")
                return
            except psycopg2.errors.UndefinedTable:
                time.sleep(1)
    raise Exception(f"{table_name} 테이블이 timeout 내에 생성되지 않았습니다.")


def copy_upsert_table(table, temp_conn, main_conn, pk="id", logger=None):
    with temp_conn.cursor() as cur:
        cur.execute(f"SELECT * FROM {table}")
        rows = cur.fetchall()
        col_names = [desc[0] for desc in cur.description]

    placeholders = ",".join(["%s"] * len(col_names))
    columns = ",".join([f'"{col}"' for col in col_names])
    updates = ",".join([f'"{col}"=EXCLUDED."{col}"' for col in col_names if col != pk])

    insert_query = f"""
        INSERT INTO {table} ({columns})
        VALUES ({placeholders})
        ON CONFLICT ({pk}) DO UPDATE SET {updates}
    """

    with main_conn.cursor() as cur:
        cur.executemany(insert_query, rows)
    if logger:
        logger.info(f"✅ {table} 테이블 upsert 완료 ({len(rows)} rows)")


def main():
    load_local_env()

    base_path = os.path.dirname(os.path.abspath(__file__))
    logger = setup_logging(base_path)

    MAIN_DB = get_db_config("MAIN")
    TEMP_DB = get_db_config("TEMP")
    DUMP_FILE = os.getenv("DUMP_FILE")

    if not DUMP_FILE or not os.path.exists(DUMP_FILE):
        logger.error(f"Dump file 경로 오류: {DUMP_FILE}")
        raise FileNotFoundError(f"Dump file not found: {DUMP_FILE}")

    try:
        logger.info("🚧 임시 DB 삭제 및 생성")
        subprocess.run(["dropdb", "--if-exists", TEMP_DB["dbname"]], check=True)
        subprocess.run(["createdb", TEMP_DB["dbname"]], check=True)

        logger.info("📥 Dump 파일 import 시작")
        subprocess.run(["psql", "-d", TEMP_DB["dbname"], "-f", DUMP_FILE], check=True)

        temp_conn = psycopg2.connect(**TEMP_DB)
        main_conn = psycopg2.connect(**MAIN_DB)

        wait_for_table(temp_conn, "subregions", logger=logger)

        try:
            with main_conn:
                for table in ["regions", "subregions", "countries", "states", "cities"]:
                    copy_upsert_table(table, temp_conn, main_conn, logger=logger)
            logger.info("🎉 모든 테이블 업데이트 완료!")
        finally:
            if temp_conn and not temp_conn.closed:
                temp_conn.close()
            if main_conn and not main_conn.closed:
                main_conn.close()
            logger.info("🔌 DB 연결 종료")

    except Exception as e:
        err_msg = " ".join(str(e).splitlines())  # 줄바꿈 없애기
        logger.error(f"🚨 에러 발생: {err_msg} ({type(e).__name__})")
        # logger.error(traceback.format_exc())  # 개발 시 필요하면 활성화
        raise

    finally:
        subprocess.run(["dropdb", TEMP_DB["dbname"]], check=True)
        logger.info("🧹 임시 DB 제거 완료.")


if __name__ == "__main__":
    main()
