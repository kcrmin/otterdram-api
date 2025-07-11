package com.otterdram.otterdram.common.geo.country;

import lombok.Getter;

import java.time.ZoneId;

@Getter
public class TimeZoneInfo {

    private ZoneId zoneName;

    /**
     * @deprecated Use {@link #zoneName} to use gmtOffset
     */
    private Integer gmtOffset;

    /**
     * @deprecated Use {@link #zoneName} to use gmtOffsetName
     */
    private String gmtOffsetName;

    /**
     * @deprecated Use {@link #zoneName} to use abbreviation
     */
    private String abbreviation;

    /**
     * @deprecated Use {@link #zoneName} to use tzName
     */
    private String tzName;
}
