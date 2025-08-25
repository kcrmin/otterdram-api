package com.otterdram.otterdram.unit.spirits.company;


import com.otterdram.otterdram.domain.spirits.company.dto.CompanyBaseData;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyCreateRequest;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyResponse;
import com.otterdram.otterdram.domain.spirits.company.repository.CompanyRepository;
import com.otterdram.otterdram.domain.spirits.company.service.CompanyService;
import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;
import com.otterdram.otterdram.domain.spirits.revision.dto.RevisionResponse;
import com.otterdram.otterdram.domain.spirits.revision.repository.RevisionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompaniesServiceTest {

    private static final Long MOCK_COMPANY_ID = 100L;
    private static final Long MOCK_REVISION_ID = 200L;
    private static final Long MOCK_PARENT_COMPANY_ID = 1L;
    private static final String TEST_COMPANY_NAME = "Test Company";
    private static final String LOGO_URL = "https://example.com/logo.png";
    private static final String SCHEMA_VERSION = "1.0.0";

    @Mock CompanyRepository companyRepository;
    @Mock RevisionRepository revisionRepository;
    @InjectMocks CompanyService companyService;

    @Nested
    @DisplayName("Company Creation Tests")
    class CompanyCreationTest {
        @Test
        void 필수_필드만_지정된_경우_revision은_저장되지_않음() {
            // Arrange
            CompanyCreateRequest request = createMinimalCompanyRequest();
            Company savedCompany = createMockSavedCompany(request, DataStatus.DRAFT);
            setupMocksForSuccessfulCreation(request, savedCompany);

            // Act
            companyService.createCompany(request);

            // Assert
            verify(companyRepository).save(any(Company.class));
            verify(revisionRepository, never()).save(any(EntityRevision.class));
        }

        @Test
        void 유효한_회사_생성_요청시_메인_엔티티에는_이름과_상태만_저장() {
            // Arrange
            CompanyCreateRequest request = createFullCompanyRequest();
            Company savedCompany = createMockSavedCompany(request, DataStatus.IN_REVIEW);
            setupMocksForSuccessfulCreation(request, savedCompany);

            // Act
            companyService.createCompany(request);

            // Assert
            ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
            verify(companyRepository).save(companyCaptor.capture());

            assertCompanyHasOnlyNameAndStatus(companyCaptor.getValue(), request.companyBaseData().companyName());
        }
        private void assertCompanyHasOnlyNameAndStatus(Company capturedCompany, String expectedName) {
            // 1 ) null 필드 검증
            Assertions.assertThat(capturedCompany)
                    .hasAllNullFieldsOrPropertiesExcept("companyName", "status");

            // 2) 설정된 필드 검증
            Assertions.assertThat(capturedCompany)
                    .extracting("companyName", "status")
                    .containsExactly(expectedName, DataStatus.IN_REVIEW);
        }

        @Test
        void 중복된_이름으로_회사_생성_요청시_duplicate_예외가_발생함() {
            // Arrange
            CompanyCreateRequest request = createMinimalCompanyRequest();
            Company existingCompany = createMockSavedCompany(request, DataStatus.DRAFT);
            when(companyRepository.findByCompanyName(request.companyBaseData().companyName()))
                    .thenReturn(Optional.of(existingCompany));

            // Act
            Assertions.assertThatThrownBy(() -> companyService.createCompany(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Company with name " + request.companyBaseData().companyName() + " already exists.");

            verify(companyRepository).findByCompanyName(request.companyBaseData().companyName());
            verifyNoMoreInteractions(companyRepository, revisionRepository);
        }

        @Test
        void 유효한_회사_생성_요청시_상세_정보는_revision에_저장() {
            // Arrange
            CompanyCreateRequest request = createFullCompanyRequest();
            Company savedCompany = createMockSavedCompany(request, DataStatus.IN_REVIEW);
            EntityRevision savedRevision = createMockSavedRevision(request, savedCompany);
            setupMocksForSuccessfulCreationWithRevision(request, savedCompany, savedRevision);

            // Act
            companyService.createCompany(request);

            // Assert
            ArgumentCaptor<EntityRevision> revisionCaptor = ArgumentCaptor.forClass(EntityRevision.class);
            verify(revisionRepository).save(revisionCaptor.capture());

            assertRevisionContainsExpectedData(revisionCaptor.getValue(), request, savedCompany);

        }

        private void assertRevisionContainsExpectedData(EntityRevision revision, CompanyCreateRequest request, Company savedCompany) {
            CompanyBaseData expectedData = new CompanyBaseData(
                    request.companyBaseData().parentCompanyId(),
                    request.companyBaseData().companyLogo(),
                    request.companyBaseData().companyName(),
                    request.companyBaseData().translations(),
                    request.companyBaseData().descriptions(),
                    request.companyBaseData().independentBottler()
            );

            Assertions.assertThat(revision)
                    .extracting("entityType", "entityId", "schemaVersion", "revisionData", "diffData", "status")
                    .containsExactly(
                            RevisionTargetEntity.COMPANY,
                            savedCompany.getId(),
                            request.schemaVersion(),
                            expectedData,
                            null,
                            RevisionStatus.IN_REVIEW
                    );

        }

        @Test
        void 회사_생성시_revision이_메인_엔티티와_정상_연결() {
            // Arrange
            CompanyCreateRequest request = createFullCompanyRequest();
            Company savedCompany = createMockSavedCompany(request, DataStatus.IN_REVIEW);
            EntityRevision savedRevision = createMockSavedRevision(request, savedCompany);
            setupMocksForSuccessfulCreationWithRevision(request, savedCompany, savedRevision);

            // Act
            companyService.createCompany(request);

            // Assert
            ArgumentCaptor<EntityRevision> revisionCaptor = ArgumentCaptor.forClass(EntityRevision.class);
            verify(revisionRepository).save(revisionCaptor.capture());

            // 1) RevisionRepository에 저장된 entityId 검증
            Assertions.assertThat(revisionCaptor.getValue().getEntityId())
                    .isEqualTo(savedCompany.getId());
        }

        @Test
        void 회사_생성_응답에_예상된_응답이_반환() {
            // Arrange
            CompanyCreateRequest request = createFullCompanyRequest();
            Company savedCompany = createMockSavedCompany(request, DataStatus.IN_REVIEW);
            setupMocksForSuccessfulCreation(request, savedCompany);
            CompanyResponse expectedResponse = createExpectedCompanyResponse(request);

            // Act
            CompanyResponse actualResponse = companyService.createCompany(request);

            // Assert
            Assertions.assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponse);
        }

        @Test
        void 회사_생성시_메인_엔티티_먼저_저장_후_revision_저장() {
            // Arrange
            CompanyCreateRequest request = createFullCompanyRequest();
            Company savedCompany = createMockSavedCompany(request, DataStatus.IN_REVIEW);
            EntityRevision savedRevision = createMockSavedRevision(request, savedCompany);
            setupMocksForSuccessfulCreationWithRevision(request, savedCompany, savedRevision);

            // Act
            companyService.createCompany(request);

            // Assert
            InOrder inOrder = inOrder(companyRepository, revisionRepository);
            inOrder.verify(companyRepository).save(any(Company.class));
            inOrder.verify(revisionRepository).save(any(EntityRevision.class));
            inOrder.verifyNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("Status Transition Tests")
    class StatusTransitionTest {

        @Test
        void status가_draft인_회사에_대한_revision_생성_시_메인_엔티티와_revision의_status가_모두_in_review로_전환되는지_테스트() {
            // Arrange
            CompanyCreateRequest request = createFullCompanyRequest();
            Company existingCompany = createMockSavedCompany(request, DataStatus.DRAFT);
            EntityRevision savedRevision = createMockSavedRevision(request, existingCompany);
            setupMocksForRevisionCreation(existingCompany, savedRevision);

            // Act
            RevisionResponse response = companyService.createCompanyRevision(MOCK_COMPANY_ID, request);

            // Assert
            assertRevisionStatusTransition();
            assertCompanyStatusTransition();
        }
        private void assertRevisionStatusTransition() {
            ArgumentCaptor<EntityRevision> revisionCaptor = ArgumentCaptor.forClass(EntityRevision.class);
            verify(revisionRepository).save(revisionCaptor.capture());
            Assertions.assertThat(revisionCaptor.getValue().getStatus())
                    .isEqualTo(RevisionStatus.IN_REVIEW);
        }
        private void assertCompanyStatusTransition() {
            ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
            verify(companyRepository).save(companyCaptor.capture());
            Assertions.assertThat(companyCaptor.getValue().getStatus())
                    .isEqualTo(DataStatus.IN_REVIEW);
        }
    }


    // Helper methods to create test data
    private void setupMocksForSuccessfulCreation(CompanyCreateRequest request, Company savedCompany) {
        when(companyRepository.findByCompanyName(request.companyBaseData().companyName())).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);
    }
    private void setupMocksForSuccessfulCreationWithRevision(CompanyCreateRequest request, Company savedCompany, EntityRevision savedRevision) {
        setupMocksForSuccessfulCreation(request, savedCompany);
        when(revisionRepository.save(any(EntityRevision.class))).thenReturn(savedRevision);
    }
    private void setupMocksForRevisionCreation(Company existingCompany, EntityRevision savedRevision) {
        when(companyRepository.findByIdAndDeletedAtIsNull(existingCompany.getId())).thenReturn(Optional.of(existingCompany));
        when(revisionRepository.save(any(EntityRevision.class))).thenReturn(savedRevision);
        when(companyRepository.save(any(Company.class))).thenReturn(existingCompany);
    }

    // Test data creation methods
    private static CompanyCreateRequest createMinimalCompanyRequest() {
        CompanyCreateRequest request = new CompanyCreateRequest(
                null,
                new CompanyBaseData(
                        null,
                        null,
                        TEST_COMPANY_NAME,
                        null,
                        null,
                        null
                )
        );
        return request;
    }
    private static CompanyCreateRequest createFullCompanyRequest() {
        Map<LanguageCode, String> translations = Map.of(
                LanguageCode.JA, "テストカンパニー",
                LanguageCode.KO, "테스트 컴퍼니"
        );
        Map<LanguageCode, String> descriptions = Map.of(
                LanguageCode.JA, "テストカンパニーの説明",
                LanguageCode.KO, "테스트 컴퍼니 설명"
        );
        return new CompanyCreateRequest(
                "1.0.0",
                new CompanyBaseData(
                        MOCK_PARENT_COMPANY_ID,
                        LOGO_URL,
                        TEST_COMPANY_NAME,
                        translations,
                        descriptions,
                        true
                )
        );
    }
    private static Company createMockSavedCompany(CompanyCreateRequest request, DataStatus status) {
        return Company.builder()
                .id(MOCK_COMPANY_ID)
                .companyName(request.companyBaseData().companyName())
                .status(status)
                .build();
    }
    private static EntityRevision createMockSavedRevision(CompanyCreateRequest request, Company savedCompany) {
        return EntityRevision.builder()
                .id(MOCK_REVISION_ID)
                .entityType(RevisionTargetEntity.COMPANY)
                .entityId(savedCompany.getId())
                .schemaVersion(request.schemaVersion())
                .revisionData(new CompanyBaseData(
                        request.companyBaseData().parentCompanyId(),
                        request.companyBaseData().companyLogo(),
                        request.companyBaseData().companyName(),
                        request.companyBaseData().translations(),
                        request.companyBaseData().descriptions(),
                        request.companyBaseData().independentBottler()
                ))
                .diffData(null)
                .status(RevisionStatus.IN_REVIEW)
                .build();
    }
    private static CompanyResponse createExpectedCompanyResponse(CompanyCreateRequest request) {
        return new CompanyResponse(
                MOCK_COMPANY_ID,
                new CompanyBaseData(
                        null,
                        null,
                        request.companyBaseData().companyName(),
                        null,
                        null,
                        null
                ),
                DataStatus.IN_REVIEW
        );
    }
}
