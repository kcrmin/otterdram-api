package com.otterdram.otterdram.testsupport;

import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyBaseData;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyCreateRequest;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyRevisionPayload;
import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;

import java.util.Map;

public final class Fixtures {
    private Fixtures() {}

    public static CompanyCreateRequest companyReqMinimal() {
        return new CompanyCreateRequest(
            null,
            new CompanyBaseData(
                null,
                null,
                "Test Company",
                null,
                null,
                null
            )
        );
    }
    public static CompanyCreateRequest companyReqFull() {
        return new CompanyCreateRequest(
            "1.0.0",
            new CompanyBaseData(
                1L,
                "http://example.com/logo.png",
                "Test Company",
                Map.of(LanguageCode.JA, "テストカンパニー",
                        LanguageCode.KO, "테스트 컴퍼니"),
                Map.of(LanguageCode.JA, "テストカンパニーの説明",
                        LanguageCode.KO, "테스트 컴퍼니 설명"),
                true
            )
        );
    }

    public static Company company(Long id, String name) {
        return Company.builder()
                .id(id)
                .companyName(name)
                .build();
    }
    public static Company company(Long id, String name, DataStatus status) {
        return Company.builder()
                .id(id)
                .companyName(name)
                .status(status)
                .build();
    }

    public static EntityRevision revision(Long id, Company c, CompanyCreateRequest req) {
        return EntityRevision.builder()
            .id(id)
            .entityType(RevisionTargetEntity.COMPANY)
            .entityId(c.getId())
            .schemaVersion(req.schemaVersion())
            .revisionData(new CompanyRevisionPayload(req.companyBaseData(), c.getStatus()))
            .diffData(null)
            .build();
    }
    public static EntityRevision revision(Long id, Company c, CompanyCreateRequest req, DataStatus snapshotStatus) {
        return EntityRevision.builder()
            .id(id)
            .entityType(RevisionTargetEntity.COMPANY)
            .entityId(c.getId())
            .schemaVersion(req.schemaVersion())
            .revisionData(new CompanyRevisionPayload(req.companyBaseData(), snapshotStatus))
            .diffData(null)
            .build();
    }
}
