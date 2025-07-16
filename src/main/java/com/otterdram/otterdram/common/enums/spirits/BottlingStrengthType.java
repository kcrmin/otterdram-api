package com.otterdram.otterdram.common.enums.spirits;

public enum BottlingStrengthType {
    CASK_STRENGTH,   // 물 희석 없이 병입 (Barrel Proof와 동일)
    FULL_PROOF,      // 캐스크에 넣을 때의 도수로 희석해 병입
    OVERPROOF,       // 일반적인 제품보다 높은 도수로 병입
    STANDARD,        // 일반적인 희석 병입 방식
    UNDERPROOF,      // 일반적인 기준보다 낮은 도수로 병입
}
