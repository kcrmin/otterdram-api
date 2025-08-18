# OtterDram Unified TDD Checklist (Integration + Service/Unit)

Version: 1.0 (Unified)  
Generated: 2025-08-16  
Scope: API (Controller / HTTP Contract / Security), Service (Business Rules / Transitions), Domain Validation, Persistence Specs, Non-Functional (Optimistic Lock, N+1)  
Purpose: 단일 문서로 전 기능 커버리지 & CI 품질 게이트를 정의하여 TDD 사이클 (Red→Green→Refactor)을 일관되게 수행.

---
## 0. Core Principles
1. Specification-as-Checklist: 아래 모든 `- [ ]` 항목은 테스트 메서드명(@DisplayName) == 시나리오 명.
2. Fast → Broad → Deep: Service(Unit) P1 → Integration Core → 나머지 Integration → Cross-cutting(동시성/성능) → 선택(E2E, 캐시 등).
3. Duplication Policy: 동일 시나리오가 Unit & Integration 모두 존재하는 경우 Unit 은 빠른 회귀 / Integration 은 계약(보안, 직렬화) 검증 용도.
4. Idempotency & Determinism: Clock / UUID / SecurityContext / External I/O mock 고정.
5. Fail Early: Validation & 권한 실패는 Service 계층에서 우선 검증, Controller 단에서는 바인딩/직렬화 오류 중심.
6. Observability: 실패 응답은 traceId 포함, 상태전이 성공 시 timestamp(set) 검증.

---
## 1. Directory Layout (권장)
```
src/test/java/com/otterdram/api/
  unit/            (pure logic, service, spec, validation)
    common/ ...
    spirits/... (companies, distilleries, ...)
    ugc/... (shelves, reviews, ...)
    user/... (accounts, follows, blocks)
    reports/
    geo/
  integration/     (@SpringBootTest + Testcontainers + Security Filter Chain)
    (동일한 패키지 구조)
  e2e/ (선택: RestAssured / Karate 실제 HTTP 흐름)
```

---
## 2. Naming Conventions
- Scenario: `given_<precondition>_when_<action>_then_<result>` (snake_case)
- Service/Unit 변형 허용: `<serviceMethod>_given_<...>_when_<...>_then_<...>` (선택)
- Integration Test Class Suffix: `*ControllerIT`, `*WorkflowIT`, `*StateTransitionIT`
- Unit/Service Test Class Suffix: `*ServiceTest`, `*RepositoryTest`, `*ValidationTest`, `*SpecificationTest`, `*ListenerTest`

---
## 3. Tagging & Priority (Service/Unit Checklist)
Format: `- [ ] scenario_name  [P1|TAG1,TAG2]`  
Priorities: P1(핵심/회귀 위험), P2(중요), P3(선택/확장)  
Common Tags: CRUD, READ, UPDATE, DELETE, VAL, FIL, TR(Transition), SEC, IDEM, CONC, EDGE, INV, MAP, REF, SPEC, PERF, CACHE, OPT, EVENT

### 3.1 Tag 상세 설명 (한국어)
각 태그는 테스트 시나리오 목적/관심사를 빠르게 식별하기 위한 메타 데이터입니다.

| 태그 | 설명 |
| ---- | ---- |
| CRUD | 생성/기본 생성 로직 검증 (Create 중심, 일부는 초기 상태 세팅 포함) |
| READ | 조회/목록/필터/정렬 결과 검증 (권한/soft delete 미노출 포함) |
| UPDATE | PUT/PATCH 변경 및 부분 갱신 로직, 버전/필드 병합 규칙 검증 |
| DELETE | Soft delete / 물리 삭제 / 멱등 재호출 동작 검증 |
| VAL | 입력값/비즈니스 검증 실패(ValidationException 등) 경로 집중 |
| FIL | 검색/필터 조합/대소문자/부분일치 등 질의 조건 동작 검증 |
| TR | 상태 전이(State Transition) / 승인/거절 / 토큰 상태 변경 등 |
| SEC | 인증/인가(권한 부족, 다른 사용자 접근 차단) 보안 관련 케이스 |
| IDEM | 멱등성: 동일 요청 반복 시 부작용 없는지 (재삭제, 중복 승인 등) |
| CONC | 동시성: 경쟁 조건, 락/버전 충돌, 레이스 상황 처리 |
| EDGE | 경계/예외/희귀 케이스 (null, 빈값, 최대/최소, 잘못된 parent 등) |
| INV | 불변식(Invariant) 유지 (ex: 삭제 제외, isLatest 단일성) |
| MAP | 파생/계산/매핑 필드 생성 (claimToken, path, defaults) |
| REF | 관계/연관 매핑 (다:다, 외래키, 연결 테이블) |
| SPEC | Specification / Query DSL / Builder 로직 자체 검증 |
| PERF | 성능/쿼리 수(N+1 방지), 핵심 경로 실행 시간 기준 회귀 방지 |
| CACHE | 캐시 적중/무효화 전략 검증 (선택적) |
| OPT | 최적화/확장 기능(복구, 배치, 캐시 등) - 핵심 아닌 부가 |
| EVENT | 도메인 이벤트 발행/구독/리스너 부작용 검증 |

### 3.2 Tag 별 대표 기대 HTTP 상태코드
설명: 각 태그가 붙은 시나리오에서 주로 검증(또는 최소 기대)하는 상태코드 패턴. 실제 도메인 정책으로 다를 수 있으므로 테스트 작성 시 구체화.

| 태그 | 주로 기대하는 상태코드 |
| ---- | ---------------------- |
| `CRUD` (Create) | 201 Created / 409 Conflict(중복) |
| `READ` | 200 OK / 404 Not Found(soft delete/권한 미노출) |
| `UPDATE` | 200 OK / 204 No Content / 400(검증 실패) / 409(버전 충돌) |
| `DELETE` | 204 No Content / 404 Not Found(케이스에 따라) |
| `VAL` | 400 Bad Request |
| `SEC` | 401 Unauthorized / 403 Forbidden |
| `TR` | 200/204 OK, 409 Conflict(금지 전이/중복 승인 정책에 따라) |
| `CONC` | 409 Conflict(Optimistic Lock) |
| `EDGE` | 400/404/422(정책에 따라), 500 내부 예외 래핑 금지 확인 |
| `PERF` | (상태코드보다 쿼리 수/시간이 핵심) |

---
## 4. Global Invariants (Cross-Cutting)
I1. Soft deleted 엔티티는 기본 조회/목록 제외 (includeDeleted 명시 시 포함)  
I2. Idempotent operations: 재삭제, 이미 승인된 revision 승인, 중복 관계 삭제 등 side-effect 없음  
I3. 허용되지 않은 상태 전이는 ValidationException 또는 AccessDenied  
I4. Revision 동일 엔티티는 단 하나만 `isLatest=true`  
I5. Tri-state (예: limitedEdition=unknown) 검색은 null 포함  
I6. Pagination: 기본 page=0 size=50, size>max → ValidationException  
I7. Validation 실패는 ValidationException / 존재하지 않음 NotFound / 중복 Duplicate / 권한 AccessDenied  
I8. Soft delete 후 동일 natural key 재생성은 현재 Duplicate 처리 (정책 문서화)  
I9. EntityTag uniqueness: (entityType, entityId, languageCode, tag)  
I10. Category path materialized, 재계산 일관  

---
## 5. Execution Order (CI Stages)
1. Unit P1 (초단위)  
2. Integration Core (Security, Companies, Revisions, Releases)  
3. Remaining Integration (Catalog, UGC, User, Reports)  
4. Concurrency & Performance (Optimistic Lock, N+1)  
5. Optional E2E / Cache / Restore Features  

---
## 6. Automation (Quality Gate)
Parse all occurrences of regex: `^- \[ \] (?<name>[a-z0-9_]+)` across this file.
1. Collect executed tests: methodName + @DisplayName.
2. Normalize: lower-case, trim, collapse multiple underscores.
3. Match scenarios; produce coverage by Priority.
4. Gates:  
   - 100% of P1 required  
   - ≥80% of P2 (warn if below)  
   - P3 informational  
5. Fail build if unmet.  
6. Allow mapping file (YAML) for composite tests covering multiple predicates.  
7. Output markdown summary artifact in CI (attach to PR).  
Pseudo (Kotlin snippet idea):
```kotlin
val required = parseChecklist()
val executed = scanJUnitXmlReports()
val (ok, missing) = diff(required, executed)
if (missing.any { it.priority == P1 }) fail()
```

---
## 7. Service / Unit Test Checklist (Tagged)
(아래는 test_checklist2.md 내용을 통합; 우선 P1 먼저 구현.)
(아래는 test_checklist2.md 내용을 통합; 우선 P1 먼저 구현.)

### 7.0 Common Utilities
> SecurityContextUtilTest (Unit)
- [ ] getCurrentUserId_인증없음_then_예외  [P1|SEC,EDGE]
- [ ] getCurrentUserId_인증있음_then_정상반환  [P1|SEC]
- [ ] hasRole_ADMIN_권한없음_then_false  [P2|SEC]
- [ ] hasRole_ADMIN_권한있음_then_true  [P2|SEC]
> PaginationAssemblerTest (Unit)
- [ ] assemble_기본값_page_size_null_then_page0_size50  [P1|SPEC]
- [ ] assemble_size_최대초과_then_ValidationException  [P1|VAL]
- [ ] assemble_sort_미지정_then_default_id_ASC  [P2|SPEC]
> IdGeneratorTest (Unit)
- [ ] generate_연속호출_then_중복없음  [P2|INV]
- [ ] generate_길이및포맷_then_UUIDv4_패턴  [P2|VAL]
> ValidatorTest (Unit)
- [ ] validate_올바른값_then_성공  [P2|VAL]
- [ ] validate_잘못된값_then_ValidationException  [P1|VAL]
> SpecificationBuilderTest (Unit)
- [ ] build_기본_then_excludes_deleted  [P1|SPEC]
- [ ] build_includeDeleted_true_then_includes_deleted  [P2|SPEC]
> ApprovalTransitionServiceTest (Unit)
- [ ] approve_IN_REVIEW_then_CONFIRMED  [P1|TR]
- [ ] reject_IN_REVIEW_then_REJECTED  [P1|TR]
- [ ] view_SUPPRESSED_일반사용자_then_accessDenied  [P1|SEC,TR]
- [ ] approve_ALREADY_CONFIRMED_then_idempotent  [P2|TR,IDEM]

### 7.1 CompaniesService
> CompaniesServiceTest (Unit)
- [ ] given_valid_request_when_create_company_then_main_entity_has_only_name_and_status
- [ ] given_valid_request_when_create_company_then_detailed_info_stored_in_revision
- [ ] given_valid_request_when_create_company_then_revision_linked_to_company
- [ ] given_valid_request_when_create_company_then_returns_correct_response
- [ ] given_valid_request_when_create_company_then_saves_in_correct_order


- [ ] create_중복이름_then_DuplicateException  [P1|CRUD,VAL]
- [ ] get_public_CONFIRMED_only_then_IN_REVIEW_비노출  [P1|READ,FIL,SEC]
- [ ] patch_null필드포함_then_null_덮어쓰기  [P2|UPDATE]
- [ ] patch_권한없음_then_AccessDenied  [P1|SEC,UPDATE]
- [ ] delete_first_then_softDelete_timestamp  [P1|DELETE]
- [ ] delete_이미_deleted_then_idempotent_no_error  [P2|IDEM,DELETE]
- [ ] get_soft_deleted_then_NotFound  [P1|READ,EDGE]
- [ ] revision_approve_then_mainEntity_status_CONFIRMED  [P1|TR]
- [ ] revision_reject_then_mainEntity_status_변경없음  [P2|TR]

### 7.2 DistilleriesService
> DistilleriesServiceTest (Unit)
- [ ] create_valid_then_status_IN_REVIEW  [P1|CRUD,TR]
- [ ] filter_company_country_city_operationalStatus_AND  [P1|FIL]
- [ ] search_q_case_insensitive  [P1|FIL]
- [ ] patch_권한없음_then_AccessDenied  [P1|SEC,UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]

### 7.3 BrandsService
> BrandsServiceTest (Unit)
- [ ] create_valid_then_IN_REVIEW  [P1|CRUD,TR]
- [ ] filter_companyId_only해당  [P1|FIL]
- [ ] search_q_partial  [P1|FIL]
- [ ] patch_null필드_then_null_overwrite  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]

### 7.4 CollectionsService
> CollectionsServiceTest (Unit)
- [ ] create_valid_then_IN_REVIEW  [P2|CRUD,TR]
- [ ] filter_brandId  [P2|FIL]
- [ ] delete_then_softDelete  [P2|DELETE]

### 7.5 ModelsService
> ModelsServiceTest (Unit)
- [ ] create_valid_then_IN_REVIEW  [P2|CRUD,TR]
- [ ] filter_collectionId_and_categoryId_AND  [P2|FIL]
- [ ] delete_then_softDelete  [P2|DELETE]

### 7.6 ReleasesService
> ReleasesServiceTest (Unit)
- [ ] create_AGE_STATED_statedAge_null_then_ValidationException  [P1|CRUD,VAL]
- [ ] create_AGE_STATED_statedAge_positive_then_ok  [P1|CRUD,VAL]
- [ ] create_NAS_statedAge_provided_then_ValidationException  [P1|CRUD,VAL]
- [ ] create_abv_0이하_then_ValidationException  [P1|CRUD,VAL]
- [ ] create_abv_100초과_then_ValidationException  [P1|CRUD,VAL]
- [ ] filter_ageStatementType_적용  [P1|FIL]
- [ ] filter_abv_range_적용  [P1|FIL]
- [ ] filter_limitedEdition_unknown_then_null포함  [P1|FIL]
- [ ] filter_peatLevel_적용  [P2|FIL]
- [ ] patch_abv_valid_then_update  [P1|UPDATE,VAL]
- [ ] delete_then_softDelete  [P1|DELETE]
- [ ] get_soft_deleted_then_NotFound  [P1|READ,EDGE]
- [ ] list_includeDeleted_adminOption_then_includes  [P2|READ,FIL,SEC]

### 7.7 DistilleryBrandRelationService
> DistilleryBrandRelationServiceTest (Unit)
- [ ] create_first_then_success_createdBy세팅  [P1|CRUD,REF]
- [ ] create_중복_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_first_then_softDelete  [P1|DELETE]
- [ ] delete_재삭제_then_idempotent  [P2|IDEM,DELETE]
- [ ] filter_distilleryId_applied  [P2|FIL]
- [ ] filter_brandId_applied  [P2|FIL]

### 7.8 ReleaseDistilleryRelationService
> ReleaseDistilleryRelationServiceTest (Unit)
- [ ] create_valid_then_success  [P1|CRUD,REF]
- [ ] create_중복_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_first_then_softDelete  [P1|DELETE]
- [ ] delete_재삭제_then_idempotent  [P2|IDEM,DELETE]
- [ ] filter_releaseId  [P2|FIL]
- [ ] filter_distilleryId  [P2|FIL]

### 7.9 EntityRevisionService
> EntityRevisionServiceTest (Unit)
- [ ] create_revision_first_then_IN_REVIEW_isLatest_true  [P1|CRUD,TR,INV]
- [ ] create_revision_sameEntity_second_then_previous_isLatest_false  [P1|CRUD,TR,INV]
- [ ] approve_firstRevision_then_mainEntity_publish  [P1|TR]
- [ ] approve_alreadyApproved_then_idempotent  [P2|TR,IDEM]
- [ ] reject_revision_then_status_REJECTED_reviewedAt  [P1|TR]
- [ ] approve_권한없음_then_AccessDenied  [P1|SEC,TR]
- [ ] filter_entityType  [P2|FIL]

### 7.10 EntityTagService
> EntityTagServiceTest (Unit)
- [ ] create_unique_then_ok  [P1|CRUD,VAL]
- [ ] create_duplicate_sameEntity_lang_tag_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_first_then_softDelete  [P1|DELETE]
- [ ] delete_재삭제_then_idempotent  [P2|IDEM,DELETE]
- [ ] filter_entityType  [P2|FIL]
- [ ] filter_languageCode  [P2|FIL]

### 7.11 CategoryService
> CategoryServiceTest (Unit)
- [ ] create_root_then_depth0_path_id  [P1|CRUD,MAP]
- [ ] create_child_valid_then_depth증가_path확장  [P1|CRUD,MAP]
- [ ] create_child_잘못된_parent_then_NotFound  [P1|CRUD,EDGE]
- [ ] filter_parentId  [P2|FIL]
- [ ] filter_depth  [P2|FIL]
- [ ] filter_path_prefix_subtree  [P2|FIL]
- [ ] put_full_replace_then_all_fields_overwritten  [P2|UPDATE]
- [ ] patch_partial_then_only_specified_changed  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]
- [ ] get_soft_deleted_then_NotFound  [P1|READ,EDGE]

### 7.12 GeoReadOnlyService
> GeoReadOnlyServiceTest (Unit)
- [ ] create_attempt_then_UnsupportedOperation  [P1|EDGE]
- [ ] update_attempt_then_UnsupportedOperation  [P1|EDGE]
- [ ] delete_attempt_then_UnsupportedOperation  [P1|EDGE]
- [ ] get_notFound_then_NotFound  [P1|READ,EDGE]
- [ ] list_regions_then_ok  [P2|READ]

### 7.13 ShelfService & ShelfShareTokenService
> ShelfServiceTest (Unit)
- [ ] create_valid_then_privacy_PUBLIC_default  [P1|CRUD,SEC]
- [ ] filter_ownerId_onlyOwner  [P1|FIL,SEC]
- [ ] filter_privacy_PRIVATE_타사용자_then_제외  [P1|FIL,SEC]
- [ ] put_full_replace  [P2|UPDATE]
- [ ] patch_partial_update  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]
- [ ] get_soft_deleted_then_NotFound  [P1|READ,EDGE]
> ShelfShareTokenServiceTest (Unit)
- [ ] create_then_claimToken_발급_만료시간세팅  [P1|CRUD,MAP]
- [ ] patch_status_valid_transition_then_ok  [P1|TR]
- [ ] patch_status_invalid_transition_then_ValidationException  [P1|TR,VAL]
- [ ] delete_then_invalidate  [P1|DELETE,IDEM]
- [ ] filter_shelfId  [P2|FIL]
- [ ] filter_status  [P2|FIL]

### 7.14 ReviewService
> ReviewServiceTest (Unit)
- [ ] create_minimal_then_defaults_applied  [P1|CRUD,MAP]
- [ ] filter_releaseId  [P1|FIL]
- [ ] filter_privacy_PUBLIC_타사용자_then_포함  [P1|FIL,SEC]
- [ ] filter_privacy_PRIVATE_타사용자_then_제외  [P1|FIL,SEC]
- [ ] create_score_100초과_then_ValidationException  [P1|VAL]
- [ ] create_score_음수_then_ValidationException  [P1|VAL]
- [ ] put_full_replace  [P2|UPDATE]
- [ ] patch_partial_update  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]

### 7.15 CommentService & CommentVoteService
> CommentServiceTest (Unit)
- [ ] create_root_then_depth0_parentNull  [P1|CRUD,MAP]
- [ ] create_reply_then_depth1_parent세팅  [P1|CRUD,MAP]
- [ ] create_depth2_then_ValidationException  [P1|CRUD,VAL]
- [ ] filter_entityType  [P2|FIL]
- [ ] filter_languageCode_none_then_default_EN  [P2|FIL,MAP]
- [ ] patch_update_content  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]
> CommentVoteServiceTest (Unit)
- [ ] create_like_first_then_success  [P1|CRUD]
- [ ] create_duplicate_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_then_idempotent  [P2|DELETE,IDEM]
- [ ] filter_entityCommentId  [P2|FIL]

### 7.16 Vial / Transfer / Token Services
> VialServiceTest (Unit)
- [ ] create_valid_then_isBlind_false_default  [P1|CRUD,MAP]
- [ ] filter_releaseId  [P2|FIL]
- [ ] filter_isBlind  [P2|FIL]
- [ ] patch_completed_true_then_fieldUpdated  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]
> TransferServiceTest (Unit)
- [ ] create_valid_then_status_ACTIVE  [P1|CRUD,TR]
- [ ] patch_status_CLAIMED_valid_then_transferedAt_set  [P1|TR,MAP]
- [ ] patch_status_invalid_transition_then_ValidationException  [P1|TR,VAL]
- [ ] filter_status  [P2|FIL]
> TransferTokenServiceTest (Unit)
- [ ] create_then_claimToken_generated  [P1|CRUD,MAP]
- [ ] filter_claimType  [P2|FIL]
- [ ] delete_then_idempotent  [P2|DELETE,IDEM]

### 7.17 User / Profile / Social / Role / Mapping Services
> UserServiceTest (Unit)
- [ ] create_valid_then_verified_false_userStatus_ACTIVE_defaults  [P1|CRUD,MAP]
- [ ] filter_status  [P1|FIL]
- [ ] filter_verified  [P1|FIL]
- [ ] filter_role  [P1|FIL,SEC]
- [ ] filter_username_partial_case_insensitive  [P2|FIL]
- [ ] patch_privacy_then_updated  [P2|UPDATE,SEC]
- [ ] delete_then_softDelete  [P1|DELETE]
> ProfileServiceTest (Unit)
- [ ] create_missing_required_then_ValidationException  [P1|CRUD,VAL]
- [ ] create_valid_then_gender_UNSPECIFIED_default  [P1|CRUD,MAP]
- [ ] put_full_replace  [P2|UPDATE]
- [ ] patch_partial_update  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]
> SocialAccountServiceTest (Unit)
- [ ] create_unique_provider_providerId_then_ok  [P1|CRUD,VAL]
- [ ] create_duplicate_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_then_idempotent  [P2|DELETE,IDEM]
> RoleServiceTest (Unit)
- [ ] create_unique_roleName_then_ok  [P1|CRUD,VAL]
- [ ] create_duplicate_then_DuplicateException  [P1|CRUD,VAL]
- [ ] put_full_update_name_changed  [P2|UPDATE]
- [ ] delete_then_removed  [P1|DELETE]
> UserRoleMappingServiceTest (Unit)
- [ ] create_mapping_first_then_ok  [P1|CRUD,REF]
- [ ] create_mapping_duplicate_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_mapping_then_idempotent  [P2|DELETE,IDEM]
> FollowServiceTest (Unit)
- [ ] create_follow_then_status_PENDING  [P1|CRUD,TR]
- [ ] approve_pending_then_APPROVED  [P1|TR]
- [ ] reject_pending_then_REJECTED  [P1|TR]
- [ ] create_self_follow_then_ValidationException  [P1|VAL]
- [ ] delete_then_idempotent  [P2|DELETE,IDEM]
> BlockServiceTest (Unit)
- [ ] create_block_then_success  [P1|CRUD]
- [ ] create_self_block_then_ValidationException  [P1|VAL]
- [ ] delete_then_idempotent  [P2|DELETE,IDEM]

### 7.18 Report Services (User / Content)
> UserReportServiceTest (Unit)
- [ ] create_user_report_then_status_PENDING  [P1|CRUD,TR]
- [ ] filter_user_reporterId  [P2|FIL]
- [ ] filter_user_status  [P2|FIL]
- [ ] patch_user_report_status_transition_valid  [P1|TR]
- [ ] patch_user_report_status_invalid_then_ValidationException  [P1|TR,VAL]
> ContentReportServiceTest (Unit)
- [ ] create_content_report_then_status_PENDING  [P1|CRUD,TR]
- [ ] filter_content_targetType  [P2|FIL]
- [ ] patch_content_report_status_transition_valid  [P1|TR]
- [ ] patch_content_report_status_invalid_then_ValidationException  [P1|TR,VAL]

### 7.19 Cross / Edge / Concurrency
> CrossEdgeServiceTest (Unit)
- [ ] patch_unknown_entity_then_NotFound  [P1|EDGE,UPDATE]
- [ ] get_deleted_entity_without_includeDeleted_then_NotFound  [P1|EDGE,READ]
- [ ] list_includeDeleted_false_then_no_deleted  [P1|READ,INV]
- [ ] softDelete_concurrent_second_call_then_idempotent  [P1|CONC,IDEM]
- [ ] approve_revision_race_condition_lastWriteWins_via_versionCheck  [P1|CONC,TR]
- [ ] repository_exception_on_save_then_wrapped_DomainException  [P1|EDGE]
- [ ] optimistic_lock_version_mismatch_then_OptimisticLockException  [P2|CONC,UPDATE]

### 7.20 Extensions (Backlog)
> CacheInvalidationTest (Unit, Optional)
- [ ] approve_revision_when_cacheEnabled_then_cacheInvalidated  [P3|CACHE,TR,OPT]
> RestoreEntityServiceTest (Unit, Optional)
- [ ] restore_soft_deleted_entity_then_restored_fields  [P3|CRUD,OPT]
> BatchImportValidationServiceTest (Unit, Optional)
- [ ] batch_import_validation_then_partial_fail_strategy  [P3|CRUD,VAL,OPT]

---
## 8. Integration Test Checklist
(HTTP contract, serialization, security, persistence wiring, side-effect timestamps.)

> Unit vs Integration 구분 (요청 설명)
> Unit: 순수 비즈니스 / 규칙 / 전이 / 밸리데이션 / 사양(Pagination, Spec Builder) 로직을 외부 I/O (DB, 네트워크) 없이 메모리에서 ms 단위로 실행. Mock / Stub 으로 협력자 격리 → 실패 원인 국소화, 빠른 피드백.
> Integration: 실제 Spring Bean wiring + Security Filter + Validation + Jackson 직렬화 + JPA (Tx, Lazy/N+1, Lock) 포함 end-to-end 흐름 검사. HTTP 계약(상태코드/JSON), 권한, 트랜잭션 롤백/락, Performance(N+1)와 같은 교차 관심사를 검증.
> 왜 분리? (1) 속도: P1 Unit 전부 수 초 내 피드백 (2) 디버깅 용이: 실패 범위 축소 (3) CI 파이프라인 단계 실행 (Unit 선행 실패시 빠른 차단) (4) 중복 최소화: 규칙은 Unit 100%, Integration 은 대표/계약 케이스만 유지 (5) 안정성: Integration 수 적게 유지해 플래키 감소.
> 원칙: 동일 시나리오가 두 계층 모두에 존재해도 OK (Unit 우선), Integration 은 HTTP contract / persistence side-effect / security edge 케이스 위주.

### 8.1 Security & Error Handling
> SecurityControllerIT
- [ ] given_인증없음_when_보호엔드포인트호출_then_401
- [ ] given_만료또는위조토큰_when_API호출_then_401
- [ ] given_유효토큰_권한부족_when_관리자엔드포인트호출_then_403
- [ ] given_ADMIN토큰_when_관리자엔드포인트호출_then_200
- [ ] given_제한된쿼리파라미터_status_일반사용자_when_사용_then_403
- [ ] given_잘못된_JSON_when_요청_then_400_parse_error
- [ ] given_필수필드누락_when_생성_then_400_validation_error
- [ ] given_잘못된_enum값_when_요청_then_400_validation_error
- [ ] given_잘못된_boolean값_when_요청_then_400_validation_error
- [ ] given_traceId_요청_when_에러발생_then_에러응답에_traceId_포함

### 8.2 Listing / Pagination / Query
> PaginationListingIT
- [ ] given_리스트요청_when_page_size_생략_then_page0_size50_기본적용
- [ ] given_리스트요청_when_size_최대초과_then_400_validation_error
- [ ] given_리스트요청_when_정렬미지정_then_id_ASC_정렬
- [ ] given_검색_q_부분일치_when_요청_then_결과포함
- [ ] given_검색_q_대소문자다름_when_요청_then_같은결과
- [ ] given_마지막페이지초과_when_요청_then_빈배열_meta정상
- [ ] given_대량데이터_when_여러페이지조회_then_totalElements_totalPages_일관

### 8.3 Soft Delete & Audit
> SoftDeleteAuditIT
- [ ] given_엔티티_soft_deleted_when_일반상세조회_then_404
- [ ] given_엔티티_soft_deleted_when_ADMIN_포함옵션조회_then_200
- [ ] given_삭제요청_이미_soft_deleted_when_재삭제_then_204_멱등
- [ ] given_삭제요청_when_성공_then_deletedAt_세팅
- [ ] given_soft_deleted_when_복구요청_then_복구성공(향후)
- [ ] given_create_when_성공_then_createdAt_UTC_ISO8601Z
- [ ] given_update_when_성공_then_updatedAt_갱신
- [ ] given_응답시간필드_when_포맷검증_then_ISO8601Z

### 8.4 Concurrency & Performance
> TransactionRollbackTest (Unit)
- [ ] given_부분업데이트중_예외_when_발생_then_DB_롤백확인
> OptimisticLockIT (Integration)
- [ ] given_동시_PATCH_same_entity_when_버전충돌_then_409
- [ ] given_동시_DELETE와_PATCH_경합_when_발생_then_일관성유지
> NPlusOneDetectionIT (Integration)
- [ ] given_리스트N회호출_when_SQL수집_then_Nplus1_없음

### 8.5 Catalog (Companies→Releases)
> CompaniesControllerIT
- [ ] given_company_생성_when_POST_then_status_IN_REVIEW
- [ ] given_company_중복이름_when_POST_then_409_conflict
- [ ] given_company_status_CONFIRMED없음_when_일반목록조회_then_IN_REVIEW_비노출
- [ ] given_company_when_GET_then_id_name_status_createdAt_반환
- [ ] given_company_PATCH_null필드포함_when_전체바디전달_then_null_덮어쓰기
- [ ] given_company_PATCH_권한없음_when_요청_then_403
- [ ] given_company_DELETE_when_요청_then_soft_delete
- [ ] given_company_soft_deleted_when_GET_then_404
- [ ] given_company_revision_approve_when_요청_then_status_CONFIRMED
- [ ] given_company_revision_reject_when_요청_then_status_REJECTED_메인데이터미반영
> DistilleriesControllerIT
- [ ] given_distillery_생성_when_POST_then_status_IN_REVIEW
- [ ] given_distillery_필터_companyId_when_요청_then_해당회사만
- [ ] given_distillery_필터_country_city_when_복합요청_then_AND필터
- [ ] given_distillery_필터_operationalStatus_when_요청_then_정상작동
- [ ] given_distillery_검색_q_when_부분일치_then_포함
- [ ] given_distillery_PATCH_권한없음_when_요청_then_403
- [ ] given_distillery_DELETE_when_요청_then_soft_delete
> BrandsControllerIT
- [ ] given_brand_생성_when_POST_then_status_IN_REVIEW
- [ ] given_brand_필터_companyId_when_요청_then_회사브랜드만
- [ ] given_brand_검색_q_when_부분일치_then_포함
- [ ] given_brand_DELETE_when_요청_then_soft_delete
- [ ] given_brand_PATCH_null필드_when_요청_then_null_덮어쓰기
> CollectionsControllerIT
- [ ] given_collection_생성_when_POST_then_status_IN_REVIEW
- [ ] given_collection_필터_brandId_when_요청_then_브랜드하위만
- [ ] given_collection_DELETE_when_요청_then_soft_delete
> ModelsControllerIT
- [ ] given_model_생성_when_POST_then_status_IN_REVIEW
- [ ] given_model_필터_collection_category_when_요청_then_AND필터
- [ ] given_model_DELETE_when_요청_then_soft_delete
> ReleasesControllerIT
- [ ] given_release_AGE_STATED_statedAge_null_when_POST_then_400
- [ ] given_release_AGE_STATED_statedAge_양수_when_POST_then_201
- [ ] given_release_NAS_statedAge_제공_when_POST_then_400
- [ ] given_release_abv_0이하_when_POST_then_400
- [ ] given_release_abv_100초과_when_POST_then_400
- [ ] given_release_PATCH_abv변경_when_요청_then_검증통과
- [ ] given_release_DELETE_when_요청_then_soft_delete
- [ ] given_release_soft_deleted_when_일반조회_then_404
- [ ] given_release_soft_deleted_when_ADMIN포함옵션_then_200
- [ ] given_release_필터_ageStatementType_when_요청_then_적용
- [ ] given_release_필터_abv범위_when_요청_then_범위내결과
- [ ] given_release_필터_limitedEdition_unknown_when_요청_then_null포함
- [ ] given_release_필터_peatLevel_when_요청_then_적용

### 8.6 Relations
> DistilleryBrandRelationControllerIT (TODO 생성)
- [ ] given_relation_distillery_brand_POST_when_정상_then_createdBy저장
- [ ] given_relation_distillery_brand_POST_중복_then_409
- [ ] given_relation_distillery_brand_DELETE_재삭제_then_204
- [ ] given_relation_distillery_brand_필터_distilleryId_then_적용
- [ ] given_relation_distillery_brand_필터_brandId_then_적용
> ReleaseDistilleryRelationControllerIT (TODO 생성)
- [ ] given_relation_release_distillery_POST_when_정상_then_생성
- [ ] given_relation_release_distillery_POST_중복_then_409
- [ ] given_relation_release_distillery_DELETE_재삭제_then_204
- [ ] given_relation_release_distillery_필터_releaseId_then_적용
- [ ] given_relation_release_distillery_필터_distilleryId_then_적용

### 8.7 Revisions / Tags / Categories
> RevisionControllerIT
- [ ] given_revision_POST_when_생성_then_status_IN_REVIEW_isLatest_true
- [ ] given_revision_새Revision_sameEntity_when_생성_then_이전_isLatest_false
- [ ] given_revision_APPROVE_when_요청_then_status_APPROVED_reviewedAt세팅
- [ ] given_revision_REJECT_when_요청_then_status_REJECTED_reviewedAt세팅
- [ ] given_revision_approve_비관리자_when_요청_then_403
- [ ] given_revision_필터_entityType_when_요청_then_적용
- [ ] given_revision_APPROVE_첫승인_when_요청_then_메인데이터_갱신
- [ ] given_revision_APPROVE_이미승인_when_요청_then_409또는_idempotent검증
> EntityTagControllerIT
- [ ] given_entity_tag_POST_when_정상_then_생성
- [ ] given_entity_tag_POST_중복_sameEntity_lang_tag_then_409
- [ ] given_entity_tag_DELETE_재삭제_then_204
- [ ] given_entity_tag_필터_entityType_then_적용
- [ ] given_entity_tag_필터_languageCode_then_적용
> CategoryControllerIT
- [ ] given_category_root_POST_when_생성_then_depth0_path_id
- [ ] given_category_child_POST_when_생성_then_depth증가_path확장
- [ ] given_category_POST_잘못된_parentId_then_404
- [ ] given_category_필터_parentId_when_요청_then_자식만
- [ ] given_category_필터_depth_when_요청_then_해당깊이
- [ ] given_category_필터_path_prefix_when_요청_then_하위전체
- [ ] given_category_PUT_full_update_when_요청_then_전체치환
- [ ] given_category_PATCH_partial_update_when_요청_then_부분갱신
- [ ] given_category_DELETE_when_요청_then_soft_delete
- [ ] given_category_soft_deleted_when_조회_then_404

### 8.8 GEO (Read-only)
> GeoControllerIT
- [ ] given_geo_regions_POST_when_요청_then_405
- [ ] given_geo_regions_PUT_when_요청_then_405
- [ ] given_geo_regions_DELETE_when_요청_then_405
- [ ] given_geo_regions_GET_없는id_then_404
- [ ] given_geo_regions_GET_then_200

### 8.9 UGC (Shelves / Tokens / Reviews / Comments / Votes / Vials / Transfers)
> ShelfControllerIT
- [ ] given_shelf_POST_when_생성_then_privacy_PUBLIC기본
- [ ] given_shelf_필터_ownerId_when_요청_then_소유자만
- [ ] given_shelf_필터_privacy_PRIVATE_when_타사용자조회_then_미포함
- [ ] given_shelf_PUT_full_update_when_요청_then_전체치환
- [ ] given_shelf_PATCH_partial_update_when_요청_then_부분갱신
- [ ] given_shelf_DELETE_when_요청_then_soft_delete
- [ ] given_shelf_soft_deleted_when_조회_then_404
> ShelfShareTokenControllerIT
- [ ] given_shelf_share_token_POST_when_생성_then_claimToken발급
- [ ] given_shelf_share_token_PATCH_status변경_when_요청_then_전이검증
- [ ] given_shelf_share_token_DELETE_when_요청_then_폐기
- [ ] given_shelf_share_token_필터_shelfId_when_요청_then_적용
- [ ] given_shelf_share_token_필터_status_when_요청_then_적용
> ReviewControllerIT
- [ ] given_review_POST_minimal_when_생성_then_기본값적용
- [ ] given_review_필터_releaseId_when_요청_then_적용
- [ ] given_review_필터_privacy_PUBLIC_when_타사용자조회_then_포함
- [ ] given_review_필터_privacy_PRIVATE_when_타사용자조회_then_미포함
- [ ] given_review_score_100초과_when_POST_then_400
- [ ] given_review_score_음수_when_POST_then_400
- [ ] given_review_PUT_full_update_when_요청_then_전체치환
- [ ] given_review_PATCH_partial_update_when_요청_then_부분갱신
- [ ] given_review_DELETE_when_요청_then_soft_delete
> CommentControllerIT
- [ ] given_comment_root_POST_when_생성_then_depth0_parentNull
- [ ] given_comment_reply_POST_when_생성_then_depth1_parent설정
- [ ] given_comment_depth2_POST_when_시도_then_400
- [ ] given_comment_필터_entityType_when_요청_then_적용
- [ ] given_comment_필터_languageCode_생략_when_요청_then_EN기본
- [ ] given_comment_PATCH_update_when_요청_then_내용변경
- [ ] given_comment_DELETE_when_요청_then_soft_delete
> CommentVoteControllerIT
- [ ] given_comment_vote_LIKE_POST_when_생성_then_성공
- [ ] given_comment_vote_POST_중복_then_409
- [ ] given_comment_vote_DELETE_when_요청_then_멱등
- [ ] given_comment_vote_필터_entityCommentId_when_요청_then_적용
> VialControllerIT
- [ ] given_vial_POST_when_생성_then_isBlind_false기본
- [ ] given_vial_필터_releaseId_when_요청_then_적용
- [ ] given_vial_필터_isBlind_when_요청_then_적용
- [ ] given_vial_PATCH_completed_true_when_요청_then_completed갱신
- [ ] given_vial_DELETE_when_요청_then_soft_delete
> VialTransferControllerIT
- [ ] given_vial_transfer_POST_when_생성_then_status_ACTIVE
- [ ] given_vial_transfer_PATCH_status_CLAIMED_when_요청_then_transferedAt세팅
- [ ] given_vial_transfer_PATCH_status_잘못된전이_then_400
- [ ] given_vial_transfer_필터_status_when_요청_then_적용
> VialTransferTokenControllerIT
- [ ] given_vial_transfer_token_POST_when_생성_then_claimToken발급
- [ ] given_vial_transfer_token_필터_claimType_when_요청_then_적용
- [ ] given_vial_transfer_token_DELETE_when_요청_then_멱등

### 8.10 Users / Profiles / Social / Roles / Follows / Blocks
> UserControllerIT
- [ ] given_user_POST_when_생성_then_verified_false_userStatus_ACTIVE
- [ ] given_user_필터_status_when_요청_then_적용
- [ ] given_user_필터_verified_when_요청_then_적용
- [ ] given_user_필터_role_when_요청_then_적용
- [ ] given_user_필터_username_부분일치_when_요청_then_포함
- [ ] given_user_PATCH_privacy변경_when_요청_then_갱신
- [ ] given_user_DELETE_when_요청_then_soft_delete
> ProfileControllerIT
- [ ] given_user_profile_POST_필수누락_then_400
- [ ] given_user_profile_POST_when_생성_then_gender_UNSPECIFIED기본
- [ ] given_user_profile_PUT_full_update_when_요청_then_전체치환
- [ ] given_user_profile_PATCH_partial_update_when_요청_then_부분갱신
- [ ] given_user_profile_DELETE_when_요청_then_soft_delete
> SocialAccountControllerIT
- [ ] given_user_social_account_POST_when_생성_then_성공
- [ ] given_user_social_account_POST_중복_provider_providerId_then_409
- [ ] given_user_social_account_DELETE_when_요청_then_멱등
> RoleControllerIT
- [ ] given_role_POST_when_생성_then_roleName_유니크저장
- [ ] given_role_POST_중복_then_409
- [ ] given_role_PUT_full_update_when_요청_then_이름변경
- [ ] given_role_DELETE_when_요청_then_삭제
> UserRoleControllerIT
- [ ] given_user_role_POST_when_생성_then_매핑생성
- [ ] given_user_role_POST_중복_then_409
- [ ] given_user_role_DELETE_when_요청_then_멱등
> FollowControllerIT
- [ ] given_follow_POST_when_생성_then_status_PENDING
- [ ] given_follow_PATCH_APPROVE_when_요청_then_status_APPROVED
- [ ] given_follow_PATCH_REJECT_when_요청_then_status_REJECTED
- [ ] given_follow_POST_self_follow_then_400
- [ ] given_follow_DELETE_when_요청_then_멱등
> BlockControllerIT
- [ ] given_block_POST_when_생성_then_차단저장
- [ ] given_block_POST_self_block_then_400
- [ ] given_block_DELETE_when_요청_then_멱등

### 8.11 Reports & Status Transitions
> ReportControllerIT
- [ ] given_user_report_POST_when_생성_then_status_PENDING
- [ ] given_user_report_필터_reporterId_when_요청_then_적용
- [ ] given_user_report_필터_status_when_요청_then_적용
- [ ] given_content_report_POST_when_생성_then_status_PENDING
- [ ] given_content_report_필터_targetType_when_요청_then_적용
- [ ] given_user_report_PATCH_status변경_when_요청_then_전이검증
- [ ] given_content_report_PATCH_status변경_when_요청_then_전이검증
- [ ] given_dataStatus_IN_REVIEW_when_APPROVE_then_CONFIRMED
- [ ] given_dataStatus_IN_REVIEW_when_REJECT_then_REJECTED
- [ ] given_dataStatus_SUPPRESSED_when_일반조회_then_비노출

## 9. Mapping Unit ↔ Integration (Coverage Matrix)
Representative overlapping scenarios (예: release validation, revision transitions) MUST exist at least in Unit; Integration duplicates focus on HTTP & persistence. CI 파서는 중복 허용.

| Domain | Core Scenarios | Unit Required | Integration Required |
|--------|----------------|---------------|----------------------|
| Releases | AGE_STATED rules, abv bounds | Yes (P1) | Yes (API contract) |
| Revisions | approve/reject, isLatest toggle | Yes | Yes |
| SoftDelete | idempotent delete, filtered queries | Yes | Yes |
| Pagination | defaults, max size | Yes | Yes |

(전체 매트릭스 자동 생성 가능: 시나리오 prefix 기반 group.)

---
## 10. TDD Workflow Quickstart
1. Add new scenario line here (unchecked).  
2. Write failing Unit test (Red).  
3. Implement minimal code (Green).  
4. Refactor (keep tests green).  
5. Add/adjust Integration test if external contract touched.  
6. Run checklist parser → ensure scenario recognized.  
7. Commit: message includes `[test:<scenario>]` for traceability.

---
## 11. Future Enhancements
- Auto-generate coverage badge per priority group.
- Export unchecked P1 scenarios as GitHub Issues (label: `tdd-missing`).
- Mutation testing (PIT) gate for critical services (threshold 70% line / 60% mutation survive).  
- Performance regression harness capturing query counts (baseline file diff).

---
(End of unified_tdd_checklist.md)
