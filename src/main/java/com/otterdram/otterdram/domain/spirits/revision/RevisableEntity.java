package com.otterdram.otterdram.domain.spirits.revision;

import com.otterdram.otterdram.common.enums.common.DataStatus;

public interface RevisableEntity {
    Long getId();
    DataStatus getStatus();
    void updateStatus(DataStatus status);
}
