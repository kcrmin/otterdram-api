package com.otterdram.otterdram.unit.spirits.company;


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
import com.otterdram.otterdram.domain.spirits.revision.dto.CompanyRevisionPayload;
import com.otterdram.otterdram.domain.spirits.revision.repository.RevisionRepository;
import org.assertj.core.api.Assertions;
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

    @Mock CompanyRepository companyRepository;
    @Mock RevisionRepository revisionRepository;
    @InjectMocks CompanyService companyService;

    @Test
    void create_valid_request_then_main_entity_only_name_status() {
        // Arrange
        CompanyCreateRequest request = createTestCompanyRequest();

        Company savedCompany = createMockSavedCompany(request);

        when(companyRepository.findByCompanyName(request.companyName())).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);


        // Act
        companyService.createCompany(request);


        // Assert
        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(companyCaptor.capture());

        Company capturedCompany = companyCaptor.getValue();

        // 1 ) null 필드 검증
        Assertions.assertThat(capturedCompany)
                .hasAllNullFieldsOrPropertiesExcept("companyName", "status");

        // 2) 설정된 필드 검증
        Assertions.assertThat(capturedCompany)
                .extracting("companyName", "status")
                .containsExactly(request.companyName(), DataStatus.IN_REVIEW);
    }

    @Test
    void create_valid_request_then_detailed_info_in_revision() {
        // Arrange
        CompanyCreateRequest request = createTestCompanyRequest();

        Company savedCompany = createMockSavedCompany(request);
        EntityRevision savedRevision = createMockSavedRevision(request, savedCompany);

        when(companyRepository.findByCompanyName(request.companyName())).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);
        when(revisionRepository.save(any(EntityRevision.class))).thenReturn(savedRevision);


        // Act
        companyService.createCompany(request);


        // Assert
        ArgumentCaptor<EntityRevision> revisionCaptor = ArgumentCaptor.forClass(EntityRevision.class);
        verify(revisionRepository).save(revisionCaptor.capture());

        EntityRevision capturedRevision = revisionCaptor.getValue();

        // 1) RevisionRepository에 저장된 EntityRevision 검증
        Assertions.assertThat(capturedRevision)
                .extracting("entityType", "entityId", "schemaVersion", "revisionData", "diffData", "status")
                .containsExactly(
                        RevisionTargetEntity.COMPANY,
                        savedCompany.getId(),
                        request.schemaVersion(),
                        new CompanyRevisionPayload(
                                request.parentCompanyId(),
                                request.companyLogo(),
                                request.companyName(),
                                request.translations(),
                                request.descriptions(),
                                request.independentBottler()
                        ).toMap(),
                        null,
                        RevisionStatus.IN_REVIEW
                );

    }

    @Test
    void create_valid_request_then_revision_linked_to_entity() {
        // Arrange
        CompanyCreateRequest request = createTestCompanyRequest();

        Company savedCompany = createMockSavedCompany(request);
        EntityRevision savedRevision = createMockSavedRevision(request, savedCompany);

        when(companyRepository.findByCompanyName(request.companyName())).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);
        when(revisionRepository.save(any(EntityRevision.class))).thenReturn(savedRevision);


        // Act
        companyService.createCompany(request);


        // Assert
        ArgumentCaptor<EntityRevision> revisionCaptor = ArgumentCaptor.forClass(EntityRevision.class);
        verify(revisionRepository).save(revisionCaptor.capture());

        EntityRevision capturedRevision = revisionCaptor.getValue();
        // 1) RevisionRepository에 저장된 entityId 검증
        Assertions.assertThat(capturedRevision.getEntityId())
                .isEqualTo(savedCompany.getId());
    }

    @Test
    void create_valid_request_then_correct_response_returned() {
        // Arrange
        CompanyCreateRequest request = createTestCompanyRequest();

        Company savedCompany = createMockSavedCompany(request);

        when(companyRepository.findByCompanyName(request.companyName())).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

        // 기대 응답
        CompanyResponse expectedCompanyResponse = createExpectedCompanyResponse(request);


        // Act
        CompanyResponse actualCompanyResponse = companyService.createCompany(request);


        // Assert
        Assertions.assertThat(actualCompanyResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedCompanyResponse);
    }

    @Test
    void create_valid_request_then_entities_saved_in_order() {
        // Arrange
        CompanyCreateRequest request = createTestCompanyRequest();

        Company savedCompany = createMockSavedCompany(request);
        EntityRevision savedRevision = createMockSavedRevision(request, savedCompany);

        when(companyRepository.findByCompanyName(request.companyName())).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);
        when(revisionRepository.save(any(EntityRevision.class))).thenReturn(savedRevision);


        // Act
        companyService.createCompany(request);


        // Assert
        InOrder inOrder = inOrder(companyRepository, revisionRepository);
        inOrder.verify(companyRepository).save(any(Company.class));
        inOrder.verify(revisionRepository).save(any(EntityRevision.class));
        inOrder.verifyNoMoreInteractions();
    }


    private static CompanyCreateRequest createTestCompanyRequest() {
        Map<LanguageCode, String> translations = Map.of(
                LanguageCode.JA, "テストカンパニー",
                LanguageCode.KO, "테스트 컴퍼니"
        );
        Map<LanguageCode, String> descriptions = Map.of(
                LanguageCode.JA, "テストカンパニーの説明",
                LanguageCode.KO, "테스트 컴퍼니 설명"
        );
        return new CompanyCreateRequest(
                1L,
                "https://example.com/logo.png",
                "Test Company",
                translations,
                descriptions,
                false,
                "1.0.0"
        );
    }

    private static Company createMockSavedCompany(CompanyCreateRequest request) {
        return Company.builder()
                .id(100L)
                .companyName(request.companyName())
                .build();
    }

    private static EntityRevision createMockSavedRevision(CompanyCreateRequest request, Company savedCompany) {
        Map<String, Object> companyRevisionPayload = new CompanyRevisionPayload(
                request.parentCompanyId(),
                request.companyLogo(),
                request.companyName(),
                request.translations(),
                request.descriptions(),
                request.independentBottler()
        ).toMap();
        return EntityRevision.builder()
                .id(200L)
                .entityType(RevisionTargetEntity.COMPANY)
                .entityId(savedCompany.getId())
                .schemaVersion(request.schemaVersion())
                .revisionData(companyRevisionPayload)
                .diffData(null)
                .status(RevisionStatus.IN_REVIEW)
                .build();
    }

    private static CompanyResponse createExpectedCompanyResponse(CompanyCreateRequest request) {
        return new CompanyResponse(
                100L,
                null,
                null,
                request.companyName(),
                null,
                null,
                null,
                DataStatus.IN_REVIEW
        );
    }

}
