package com.otterdram.otterdram.common.enums.spirits;

public enum DistilleryOperationalStatus {
    ACTIVE,             // 정상 운영 중
    INACTIVE,           // 일시 비가동(일시 중단, 휴업)
    CLOSED,             // 완전 폐쇄(문 닫음)
    DEMOLISHED,         // 철거됨(건물/시설 사라짐)
    UNDER_CONSTRUCTION, // 건설 중(미완공)
    PLANNED,            // 설립 예정(계획만 있음)
    MOTHBALLED,         // 장기 비가동(시설 유지/재개 가능)
    RENOVATION,         // 리노베이션(공사/개조 중)
    UNKNOWN,            // 정보 없음/불분명
}
