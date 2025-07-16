package com.otterdram.otterdram.common.enums.user;

public enum UserStatus {

    /** 정상 활동 중인 계정 */
    ACTIVE,

    /** 일시 정지된 계정 (관리자 또는 시스템에 의해 제한됨) */
    SUSPENDED,

    /** 영구 정지된 계정 (위반 등으로 강제 차단됨) */
    BANNED,

    /** 사용자가 직접 비활성화한 계정 (재활성화 가능) */
    DEACTIVATED,

    /** 완전히 삭제된 계정 (복구 불가) */
    DELETED,

}
