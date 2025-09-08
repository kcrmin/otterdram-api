package com.otterdram.otterdram.unit.spirits.company;

import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyCreateRequest;
import com.otterdram.otterdram.domain.spirits.company.repository.CompanyRepository;
import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;
import com.otterdram.otterdram.domain.spirits.revision.repository.RevisionRepository;
import com.otterdram.otterdram.domain.spirits.revision.service.RevisionService;
import com.otterdram.otterdram.testsupport.Fixtures;
import com.otterdram.otterdram.testsupport.RepositoryStubs;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CompanyRevisionApprovalRejectionTest {

    @Mock CompanyRepository companyRepository;
    @Mock RevisionRepository revisionRepository;
    @InjectMocks RevisionService revisionService;

    @Nested
    @DisplayName("승인 테스트")
    class ApprovalTests {
        private final CompanyCreateRequest request = Fixtures.companyReqFull();
        private final Company existingCompany = Fixtures.company(
            1L,
            request.companyBaseData().companyName(),
            DataStatus.IN_REVIEW
        );
        private final EntityRevision existingRevision = Fixtures.revision(
            1L,
            existingCompany,
            request
        );

        @BeforeEach
        void setUp() {
            // Arrange
            RepositoryStubs.existingCompanyById(companyRepository, existingCompany);
            RepositoryStubs.existingRevisionById(revisionRepository, existingRevision);
            RepositoryStubs.existingCompanyReferenceById(companyRepository, existingCompany);
            RepositoryStubs.saveCompany(companyRepository, any(Company.class), existingCompany);
            RepositoryStubs.saveRevision(revisionRepository, any(EntityRevision.class), existingRevision);


            // Act
            revisionService.approve(existingRevision.getId());
        }

        @Test
        @DisplayName("company status가 CONFIRMED로 전환되었는지 검증")
        void testCompanyStatusApproval() {
            // Assert
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany.getStatus()).isEqualTo(DataStatus.CONFIRMED);
        }

        @Test
        @DisplayName("revision status가 APPROVED로 전환되었는지 검증")
        void testRevisionStatusApproval() {
            // Assert
            EntityRevision capturedRevision = captureRevision();
            Assertions.assertThat(capturedRevision.getStatus()).isEqualTo(RevisionStatus.APPROVED);
        }

        @Test
        @DisplayName("company 필드 값이 리비전 데이터로 업데이트 되었는지 검증")
        void testCompanyFieldValuesUpdated() {
            // Assert
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany)
                .hasFieldOrPropertyWithValue("parentCompany", request.companyBaseData().parentCompanyId() != null ? companyRepository.getReferenceById(request.companyBaseData().parentCompanyId()) : null)
                .hasFieldOrPropertyWithValue("companyLogo", request.companyBaseData().companyLogo())
                .hasFieldOrPropertyWithValue("companyName", request.companyBaseData().companyName())
                .hasFieldOrPropertyWithValue("translations", request.companyBaseData().translations())
                .hasFieldOrPropertyWithValue("descriptions", request.companyBaseData().descriptions())
                .hasFieldOrPropertyWithValue("independentBottler", request.companyBaseData().independentBottler());

        }
    }

    @Nested
    @DisplayName("거절 테스트")
    class RejectionTests {
        private final DataStatus snapshotStatus = DataStatus.DRAFT;
        private final CompanyCreateRequest minRequest = Fixtures.companyReqMinimal();
        private final CompanyCreateRequest fullRequest = Fixtures.companyReqFull();
        private final Company existingCompany = Fixtures.company(
            1L,
            minRequest.companyBaseData().companyName(),
            DataStatus.IN_REVIEW
        );
        private final EntityRevision existingRevision = Fixtures.revision(
            1L,
            existingCompany,
            fullRequest,
            snapshotStatus
        );

        @BeforeEach
        void setUp() {
            // Arrange
            RepositoryStubs.existingCompanyById(companyRepository, existingCompany);
            RepositoryStubs.existingRevisionById(revisionRepository, existingRevision);
            RepositoryStubs.saveCompany(companyRepository, any(Company.class), existingCompany);
            RepositoryStubs.saveRevision(revisionRepository, any(EntityRevision.class), existingRevision);

            // Act
            revisionService.reject(existingRevision.getId());
        }

        @Test
        @DisplayName("company status가 원래 상태로 전환되었는지 검증")
        void testCompanyStatusRejection() {
            // Assert
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany.getStatus()).isEqualTo(snapshotStatus);
        }

        @Test
        @DisplayName("revision status가 REJECTED로 전환되었는지 검증")
        void testRevisionStatusRejection() {
            // Assert
            EntityRevision capturedRevision = captureRevision();
            Assertions.assertThat(capturedRevision.getStatus()).isEqualTo(RevisionStatus.REJECTED);
        }

        @Test
        @DisplayName("company 필드 값이 리비전 데이터로 업데이트되지 않고 기존 값 유지되었는지 검증")
        void testCompanyFieldValuesUnchanged() {
            // Assert
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany)
                .hasFieldOrPropertyWithValue("parentCompany", existingCompany.getParentCompany())
                .hasFieldOrPropertyWithValue("companyLogo", existingCompany.getCompanyLogo())
                .hasFieldOrPropertyWithValue("companyName", existingCompany.getCompanyName())
                .hasFieldOrPropertyWithValue("translations", existingCompany.getTranslations())
                .hasFieldOrPropertyWithValue("descriptions", existingCompany.getDescriptions())
                .hasFieldOrPropertyWithValue("independentBottler", existingCompany.getIndependentBottler());
        }
    }


    private Company captureCompany() {
        var captor = org.mockito.ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(captor.capture());
        return captor.getValue();
    }
    private EntityRevision captureRevision() {
        var captor = org.mockito.ArgumentCaptor.forClass(EntityRevision.class);
        verify(revisionRepository).save(captor.capture());
        return captor.getValue();
    }
}
