package com.otterdram.otterdram.unit.spirits.company;

import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;
import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyCreateRequest;
import com.otterdram.otterdram.domain.spirits.company.dto.CompanyRevisionPayload;
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
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyCreationServiceTest {

    @Mock CompanyRepository companyRepository;
    @Mock RevisionRepository revisionRepository;
    @InjectMocks CompanyService companyService;

    // minimal 데이터로 생성 요청시
    @Nested
    class CreateCompany_MinimalData {
        private final CompanyCreateRequest request = Fixtures.companyReqMinimal();

        @BeforeEach
        void setUp() {
            // Arrange
            Company savedCompany = Fixtures.company(
                    1L,
                    request.companyBaseData().companyName()
            );
            RepositoryStubs.noDuplicateName(companyRepository, request.companyBaseData().companyName());
            RepositoryStubs.saveCompany(companyRepository, any(Company.class), savedCompany);

            // Act
            companyService.createCompany(request);
        }

        @Test
        @DisplayName("companyRepository.save()가 한 번 호출되었는지 검증")
        void testCompanyRepositorySaveCalled() {
            verify(companyRepository).save(any(Company.class));
        }

        @Test
        @DisplayName("메인 엔티티에 이름과 상태만 저장됨 (나머지 필드는 null)")
        void testCompanyFields() {
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany)
                    .hasAllNullFieldsOrPropertiesExcept("id", "companyName", "status", "childCompanies", "distilleries", "brands");
        }

        @Test
        @DisplayName("연관 컬렉션은 비어있음")
        void testCompanyCollectionsEmpty() {
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany.getChildCompanies()).isEmpty();
            Assertions.assertThat(capturedCompany.getDistilleries()).isEmpty();
            Assertions.assertThat(capturedCompany.getBrands()).isEmpty();
        }

        @Test
        @DisplayName("이름과 상태 필드 값 검증")
        void testCompanyFieldValues() {
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany)
                    .hasFieldOrPropertyWithValue("companyName", request.companyBaseData().companyName())
                    .hasFieldOrPropertyWithValue("status", DataStatus.DRAFT);
        }

        @Test
        @DisplayName("리비전 엔티티는 생성되지 않음")
        void testNoRevisionCreated() {
            verify(revisionRepository, never()).save(any());
        }

    }

    // full 데이터로 생성 요청시
    @Nested
    class CreateCompany_FullData {
        private final CompanyCreateRequest request = Fixtures.companyReqFull();
        private final Company savedCompany = Fixtures.company(
            1L,
            request.companyBaseData().companyName()
        );
        private final EntityRevision savedRevision = Fixtures.revision(
            1L,
            savedCompany,
            request
        );

        @BeforeEach
        void setUp() {
            // Arrange
            RepositoryStubs.noDuplicateName(companyRepository, request.companyBaseData().companyName());
            RepositoryStubs.saveCompany(companyRepository, any(Company.class), savedCompany);
            RepositoryStubs.saveRevision(revisionRepository, any(EntityRevision.class), savedRevision);

            // Act
            companyService.createCompany(request);
        }

        @Test
        @DisplayName("companyRepository.save()와 revisionRepository.save()가 한 번씩 호출되었는지 검증")
        void testRepositoriesSaveCalled() {
            verify(companyRepository).save(any(Company.class));
            verify(revisionRepository).save(any(EntityRevision.class));
        }

        @Test
        @DisplayName("메인 엔티티에 이름과 상태만 저장됨 (나머지 필드는 null)")
        void testCompanyFields() {
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany)
                    .hasAllNullFieldsOrPropertiesExcept("id", "companyName", "status", "childCompanies", "distilleries", "brands");
        }

        @Test
        @DisplayName("연관 컬렉션은 비어있음")
        void testCompanyCollectionsEmpty() {
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany.getChildCompanies()).isEmpty();
            Assertions.assertThat(capturedCompany.getDistilleries()).isEmpty();
            Assertions.assertThat(capturedCompany.getBrands()).isEmpty();
        }

        @Test
        @DisplayName("이름과 상태 필드 값 검증")
        void testCompanyFieldValues() {
            Company capturedCompany = captureCompany();
            Assertions.assertThat(capturedCompany)
                    .hasFieldOrPropertyWithValue("companyName", request.companyBaseData().companyName())
                    .hasFieldOrPropertyWithValue("status", DataStatus.IN_REVIEW);
        }

        @Test
        @DisplayName("리비전 엔티티의 주요 필드 값 검증")
        void testRevisionFieldValues() {
            EntityRevision capturedRevision = captureRevision();

            Assertions.assertThat(capturedRevision)
                    .hasFieldOrPropertyWithValue("entityType", RevisionTargetEntity.COMPANY)
                    .hasFieldOrPropertyWithValue("entityId", savedCompany.getId())
                    .hasFieldOrPropertyWithValue("schemaVersion", request.schemaVersion())
                    .hasFieldOrPropertyWithValue("diffData", null)
                    .hasFieldOrPropertyWithValue("status", RevisionStatus.IN_REVIEW);

            // revisionData 필드 값 검증
            CompanyRevisionPayload expectedPayload = new CompanyRevisionPayload(
                    request.companyBaseData(),
                    savedCompany.getStatus()
            );
            Assertions.assertThat(capturedRevision.getRevisionData())
                    .usingRecursiveComparison()
                    .isEqualTo(expectedPayload);
        }

        @Test
        @DisplayName("저장 순서 검증")
        void testSaveOrder() {
            InOrder inOrder = inOrder(companyRepository, revisionRepository);
            inOrder.verify(companyRepository).save(any(Company.class));
            inOrder.verify(revisionRepository).save(any(EntityRevision.class));
            inOrder.verifyNoMoreInteractions();
        }

    }

    @Test
    @DisplayName("중복된_이름으로_회사_생성_요청시_duplicate_예외가_발생함")
    void testCreateCompany_DuplicateName() {
        // Arrange
        var request = Fixtures.companyReqMinimal();
        Company existingCompany = Fixtures.company(
            1L,
            request.companyBaseData().companyName()
        );
        RepositoryStubs.duplicateName(companyRepository, request.companyBaseData().companyName(), existingCompany);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> companyService.createCompany(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Company with name '" + request.companyBaseData().companyName() + "' already exists.");

        // 1. companyRepository.save()와 revisionRepository.save()가 호출되지 않음
        verify(companyRepository, never()).save(any());
        verify(revisionRepository, never()).save(any());
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
