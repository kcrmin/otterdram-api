package com.otterdram.otterdram.common.enums.common;

public enum DataStatus {

    // /** 초기 생성 상태 (비노출) */
    // DRAFT,

    /** 리비전 진행 중 상태 (노출 가능하나 최신 아님) */
    IN_REVIEW,

    /** 리비전 완료 상태 (정상 노출) */
    CONFIRMED,

    /** 신고 등으로 숨김 처리된 상태 (비노출) */
    SUPPRESSED,

}
