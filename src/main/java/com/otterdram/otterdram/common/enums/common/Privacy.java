package com.otterdram.otterdram.common.enums.common;

public enum Privacy {

    /** 전체 공개 (누구나 접근 가능) */
    PUBLIC,

    /** 팔로워에게만 공개 (로그인 필요) */
    FOLLOWERS_ONLY,

    /** 멤버 전용 공개 (그룹/커뮤니티 소속자만 접근 가능) */
    MEMBERS_ONLY,

    /** 비공개 (작성자 본인만 접근 가능) */
    PRIVATE,

}