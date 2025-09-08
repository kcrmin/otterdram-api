package com.otterdram.otterdram.unit.spirits.company;

import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyCreateRequest;
import com.otterdram.otterdram.domain.spirits.company.repository.CompanyRepository;
import com.otterdram.otterdram.domain.spirits.company.service.CompanyService;
import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;
import com.otterdram.otterdram.domain.spirits.revision.repository.RevisionRepository;
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
public class CompanyTransitionServiceTest {

    @Mock CompanyRepository companyRepository;
    @Mock RevisionRepository revisionRepository;
    @InjectMocks CompanyService companyService;

    @Nested
    @DisplayName("기존 company status가 DRAFT인 경우")
    class TransitionTests_DRAFT {
        private final CompanyCreateRequest request = Fixtures.companyReqFull();
        private final Company savedCompany = Fixtures.company(
            1L,
            request.companyBaseData().companyName(),
            DataStatus.DRAFT
        );

        @BeforeEach
        void setUp() {
            // Arrange
            RepositoryStubs.existingCompanyById(companyRepository, savedCompany);
            RepositoryStubs.saveCompany(companyRepository, any(Company.class), savedCompany);
        }

        @Test
        @DisplayName("company status가 IN_REVIEW로 전환되었는지 검증")
        void testCompanyStatusTransition() {
            // Act
            companyService.createRevision(savedCompany.getId(), request);

            // Assert
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany.getStatus()).isEqualTo(DataStatus.IN_REVIEW);
        }

        @Test
        @DisplayName("revision status가 IN_REVIEW로 전환되었는지 검증")
        void testRevisionStatusTransition() {
            // Act
            companyService.createRevision(savedCompany.getId(), request);

            // Assert
            EntityRevision capturedRevision = captureRevision();
            Assertions.assertThat(capturedRevision.getStatus()).isEqualTo(RevisionStatus.IN_REVIEW);
        }
    }

    @Nested
    @DisplayName("기존 company status가 IN_REVIEW인 경우")
    class TransitionTests_IN_REVIEW {
        private final CompanyCreateRequest request = Fixtures.companyReqFull();
        private final Company savedCompany = Fixtures.company(
            1L,
            request.companyBaseData().companyName(),
            DataStatus.IN_REVIEW
        );
        private final EntityRevision savedRevision = Fixtures.revision(
            1L,
            savedCompany,
            request
        );

        @Test
        @DisplayName("company status가 IN_REVIEW인 경우 예외 발생")
        void testCreateRevisionForInReviewCompany() {
            // Arrange
            RepositoryStubs.existingCompanyById(companyRepository, savedCompany);

            // Act & Assert
            Assertions.assertThatThrownBy(() -> {
                companyService.createRevision(savedCompany.getId(), request);
            })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot create a new revision for a company that is already under review.");
        }

        @Test
        @DisplayName("이미 IN_REVIEW 상태인 리비전이 있는 경우 예외 발생")
        void testCreateDuplicateInReviewRevision() {
            // Arrange
            RepositoryStubs.existingCompanyById(companyRepository, savedCompany);
            RepositoryStubs.existingPendingRevision(revisionRepository, savedRevision);

            // Act & Assert
            Assertions.assertThatThrownBy(() -> {
                companyService.createRevision(savedCompany.getId(), request);
            })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("There is already a pending revision for this company. Revision ID: " + savedRevision.getId());
        }


    }

    @Nested
    @DisplayName("기존 company status가 CONFIRMED인 경우")
    class TransitionTests_CONFIRMED {
        private final CompanyCreateRequest request = Fixtures.companyReqFull();
        private final Company savedCompany = Fixtures.company(
            1L,
            request.companyBaseData().companyName(),
            DataStatus.CONFIRMED
        );
        private final EntityRevision savedRevision = Fixtures.revision(
            1L,
            savedCompany,
            request
        );

        @BeforeEach
        void setUp() {
            // Arrange
            RepositoryStubs.existingCompanyById(companyRepository, savedCompany);
            RepositoryStubs.saveCompany(companyRepository, any(Company.class), savedCompany);
            RepositoryStubs.saveRevision(revisionRepository, any(EntityRevision.class), savedRevision);
        }

        @Test
        @DisplayName("company status가 IN_REVIEW로 전환되었는지 검증")
        void testCompanyStatusTransition() {
            // Act
            companyService.createRevision(savedCompany.getId(), request);

            // Assert
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany.getStatus()).isEqualTo(DataStatus.IN_REVIEW);
        }

        @Test
        @DisplayName("revision status가 IN_REVIEW로 전환되었는지 검증")
        void testRevisionStatusTransition() {
            // Act
            companyService.createRevision(savedCompany.getId(), request);

            // Assert
            EntityRevision capturedRevision = captureRevision();
            Assertions.assertThat(capturedRevision.getStatus()).isEqualTo(RevisionStatus.IN_REVIEW);
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
