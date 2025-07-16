package com.otterdram.otterdram.common.enums.common;

public enum RevisionStatus {

//    /** 초기 생성 상태 (임시 저장) */
//    DRAFT,

    /** 검토 요청됨 (제출 완료) */
    IN_REVIEW,

    /** 승인 완료 상태 (메인 반영 대상) */
    APPROVED,

    /** 반려됨 (비노출) */
    REJECTED,

}
