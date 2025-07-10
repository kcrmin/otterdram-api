"""
JSON 오류 자동 수정 모듈
"""

import re
import json
import logging
from typing import Optional, List, Dict, Any


class JSONFixer:
    """JSON 오류를 자동으로 수정하는 클래스"""

    def __init__(self, logger: Optional[logging.Logger] = None):
        self.logger = logger or logging.getLogger(__name__)

    def fix_json_error(self, json_str: str) -> Optional[str]:
        """JSON 오류를 자동으로 수정합니다.

        Args:
            json_str: 수정할 JSON 문자열

        Returns:
            수정된 JSON 문자열 또는 None (수정 불가능한 경우)
        """
        try:
            # 이미 유효한 JSON인지 확인
            json.loads(json_str)
            return json_str
        except json.JSONDecodeError:
            pass

        # 여러 수정 패턴을 시도
        fixed_json = json_str

        # 1. 잘못된 콜론 패턴 수정 (예: : "값","키" -> "키": "값")
        fixed_json = self._fix_reversed_key_value_pattern(fixed_json)

        # 2. 홀따옴표 이스케이프 처리
        fixed_json = self._fix_quote_escaping(fixed_json)

        # 3. 불완전한 키-값 쌍 제거
        fixed_json = self._remove_incomplete_pairs(fixed_json)

        # 4. 잘못된 콤마 위치 수정
        fixed_json = self._fix_comma_positions(fixed_json)

        # 5. 최종 유효성 검사
        try:
            json.loads(fixed_json)
            self.logger.debug(f"JSON 수정 성공: {json_str[:50]}...")
            return fixed_json
        except json.JSONDecodeError as e:
            self.logger.debug(f"JSON 수정 실패: {e}")
            return None

    def _fix_reversed_key_value_pattern(self, json_str: str) -> str:
        """잘못된 키-값 순서를 수정합니다.

        예: : "Mec''hiko","ko" -> "ko": "Mec''hiko"
        """
        # : "값","키" 패턴을 찾아서 "키": "값"으로 수정
        pattern = r':\s*"([^"]*)",\s*"([^"]*)"'

        def replace_func(match):
            value, key = match.groups()
            # 키가 언어 코드 형태인지 확인 (2-5자의 알파벳, 하이픈 포함)
            if re.match(r"^[a-zA-Z-]{2,5}$", key):
                return f'"{key}": "{value}"'
            else:
                return match.group(0)  # 원본 그대로 반환

        return re.sub(pattern, replace_func, json_str)

    def _fix_quote_escaping(self, json_str: str) -> str:
        """따옴표 이스케이프 문제를 수정합니다."""
        # 홀따옴표 연속을 하나로 변경
        json_str = json_str.replace("''", "'")

        # JSON 내부의 홑따옴표를 이스케이프
        # 단, 이미 이스케이프된 것은 제외
        json_str = re.sub(r"(?<!\\)'", "\\'", json_str)

        return json_str

    def _remove_incomplete_pairs(self, json_str: str) -> str:
        """불완전한 키-값 쌍을 제거합니다.

        예: ,"키" (값이 없는 경우) -> 제거
        """
        # 값이 없는 키 제거 (끝에 오는 경우)
        pattern = r',\s*"([^"]*)"(?!\s*:)'
        json_str = re.sub(pattern, "", json_str)

        # 키가 없는 값 제거 (시작에 오는 경우)
        pattern = r':\s*"([^"]*)",(?!\s*"[^"]*"\s*:)'
        json_str = re.sub(pattern, "", json_str)

        return json_str

    def _fix_comma_positions(self, json_str: str) -> str:
        """콤마 위치 문제를 수정합니다."""
        # 중복된 콤마 제거
        json_str = re.sub(r",\s*,", ",", json_str)

        # } 앞의 불필요한 콤마 제거
        json_str = re.sub(r",\s*}", "}", json_str)

        # { 뒤의 불필요한 콤마 제거
        json_str = re.sub(r"{\s*,", "{", json_str)

        return json_str

    def get_error_patterns(self) -> List[Dict[str, Any]]:
        """지원하는 오류 패턴 목록을 반환합니다."""
        return [
            {
                "name": "잘못된 키-값 순서",
                "pattern": r':\s*"([^"]*)",\s*"([^"]*)"',
                "description": ': "값","키" 형태를 "키": "값"으로 수정',
            },
            {
                "name": "홀따옴표 이스케이프",
                "pattern": r"''",
                "description": "연속된 홀따옴표를 하나로 변경",
            },
            {
                "name": "불완전한 키-값 쌍",
                "pattern": r',\s*"([^"]*)"(?!\s*:)',
                "description": "값이 없는 키를 제거",
            },
            {
                "name": "잘못된 콤마 위치",
                "pattern": r",\s*}",
                "description": "} 앞의 불필요한 콤마 제거",
            },
        ]
