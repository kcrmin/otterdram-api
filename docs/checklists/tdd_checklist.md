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
## 7. Integration Test Checklist
(HTTP contract, serialization, security, persistence wiring, side-effect timestamps.)

### 7.1 Security & Error Handling
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

### 7.2 Listing / Pagination / Query
- [ ] given_리스트요청_when_page_size_생략_then_page0_size50_기본적용
- [ ] given_리스트요청_when_size_최대초과_then_400_validation_error
- [ ] given_리스트요청_when_정렬미지정_then_id_ASC_정렬
- [ ] given_검색_q_부분일치_when_요청_then_결과포함
- [ ] given_검색_q_대소문자다름_when_요청_then_같은결과
- [ ] given_마지막페이지초과_when_요청_then_빈배열_meta정상
- [ ] given_대량데이터_when_여러페이지조회_then_totalElements_totalPages_일관

### 7.3 Soft Delete & Audit
- [ ] given_엔티티_soft_deleted_when_일반상세조회_then_404
- [ ] given_엔티티_soft_deleted_when_ADMIN_포함옵션조회_then_200
- [ ] given_삭제요청_이미_soft_deleted_when_재삭제_then_204_멱등
- [ ] given_삭제요청_when_성공_then_deletedAt_세팅
- [ ] given_soft_deleted_when_복구요청_then_복구성공(향후)
- [ ] given_create_when_성공_then_createdAt_UTC_ISO8601Z
- [ ] given_update_when_성공_then_updatedAt_갱신
- [ ] given_응답시간필드_when_포맷검증_then_ISO8601Z

### 7.4 Concurrency & Performance
- [ ] given_부분업데이트중_예외_when_발생_then_DB_롤백확인
- [ ] given_동시_PATCH_same_entity_when_버전충돌_then_409
- [ ] given_동시_DELETE와_PATCH_경합_when_발생_then_일관성유지
- [ ] given_리스트N회호출_when_SQL수집_then_Nplus1_없음

### 7.5 Catalog (Companies→Releases)
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
- [ ] given_distillery_생성_when_POST_then_status_IN_REVIEW
- [ ] given_distillery_필터_companyId_when_요청_then_해당회사만
- [ ] given_distillery_필터_country_city_when_복합요청_then_AND필터
- [ ] given_distillery_필터_operationalStatus_when_요청_then_정상작동
- [ ] given_distillery_검색_q_when_부분일치_then_포함
- [ ] given_distillery_PATCH_권한없음_when_요청_then_403
- [ ] given_distillery_DELETE_when_요청_then_soft_delete
- [ ] given_brand_생성_when_POST_then_status_IN_REVIEW
- [ ] given_brand_필터_companyId_when_요청_then_회사브랜드만
- [ ] given_brand_검색_q_when_부분일치_then_포함
- [ ] given_brand_DELETE_when_요청_then_soft_delete
- [ ] given_brand_PATCH_null필드_when_요청_then_null_덮어쓰기
- [ ] given_collection_생성_when_POST_then_status_IN_REVIEW
- [ ] given_collection_필터_brandId_when_요청_then_브랜드하위만
- [ ] given_collection_DELETE_when_요청_then_soft_delete
- [ ] given_model_생성_when_POST_then_status_IN_REVIEW
- [ ] given_model_필터_collection_category_when_요청_then_AND필터
- [ ] given_model_DELETE_when_요청_then_soft_delete
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

### 7.6 Relations
- [ ] given_relation_distillery_brand_POST_when_정상_then_createdBy저장
- [ ] given_relation_distillery_brand_POST_중복_then_409
- [ ] given_relation_distillery_brand_DELETE_재삭제_then_204
- [ ] given_relation_distillery_brand_필터_distilleryId_then_적용
- [ ] given_relation_distillery_brand_필터_brandId_then_적용
- [ ] given_relation_release_distillery_POST_when_정상_then_생성
- [ ] given_relation_release_distillery_POST_중복_then_409
- [ ] given_relation_release_distillery_DELETE_재삭제_then_204
- [ ] given_relation_release_distillery_필터_releaseId_then_적용
- [ ] given_relation_release_distillery_필터_distilleryId_then_적용

### 7.7 Revisions / Tags / Categories
- [ ] given_revision_POST_when_생성_then_status_IN_REVIEW_isLatest_true
- [ ] given_revision_새Revision_sameEntity_when_생성_then_이전_isLatest_false
- [ ] given_revision_APPROVE_when_요청_then_status_APPROVED_reviewedAt세팅
- [ ] given_revision_REJECT_when_요청_then_status_REJECTED_reviewedAt세팅
- [ ] given_revision_approve_비관리자_when_요청_then_403
- [ ] given_revision_필터_entityType_when_요청_then_적용
- [ ] given_revision_APPROVE_첫승인_when_요청_then_메인데이터_갱신
- [ ] given_revision_APPROVE_이미승인_when_요청_then_409또는_idempotent검증
- [ ] given_entity_tag_POST_when_정상_then_생성
- [ ] given_entity_tag_POST_중복_sameEntity_lang_tag_then_409
- [ ] given_entity_tag_DELETE_재삭제_then_204
- [ ] given_entity_tag_필터_entityType_then_적용
- [ ] given_entity_tag_필터_languageCode_then_적용
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

### 7.8 GEO (Read-only)
- [ ] given_geo_regions_POST_when_요청_then_405
- [ ] given_geo_regions_PUT_when_요청_then_405
- [ ] given_geo_regions_DELETE_when_요청_then_405
- [ ] given_geo_regions_GET_없는id_then_404
- [ ] given_geo_regions_GET_then_200

### 7.9 UGC (Shelves / Tokens / Reviews / Comments / Votes / Vials / Transfers)
- [ ] given_shelf_POST_when_생성_then_privacy_PUBLIC기본
- [ ] given_shelf_필터_ownerId_when_요청_then_소유자만
- [ ] given_shelf_필터_privacy_PRIVATE_when_타사용자조회_then_미포함
- [ ] given_shelf_PUT_full_update_when_요청_then_전체치환
- [ ] given_shelf_PATCH_partial_update_when_요청_then_부분갱신
- [ ] given_shelf_DELETE_when_요청_then_soft_delete
- [ ] given_shelf_soft_deleted_when_조회_then_404
- [ ] given_shelf_share_token_POST_when_생성_then_claimToken발급
- [ ] given_shelf_share_token_PATCH_status변경_when_요청_then_전이검증
- [ ] given_shelf_share_token_DELETE_when_요청_then_폐기
- [ ] given_shelf_share_token_필터_shelfId_when_요청_then_적용
- [ ] given_shelf_share_token_필터_status_when_요청_then_적용
- [ ] given_review_POST_minimal_when_생성_then_기본값적용
- [ ] given_review_필터_releaseId_when_요청_then_적용
- [ ] given_review_필터_privacy_PUBLIC_when_타사용자조회_then_포함
- [ ] given_review_필터_privacy_PRIVATE_when_타사용자조회_then_미포함
- [ ] given_review_score_100초과_when_POST_then_400
- [ ] given_review_score_음수_when_POST_then_400
- [ ] given_review_PUT_full_update_when_요청_then_전체치환
- [ ] given_review_PATCH_partial_update_when_요청_then_부분갱신
- [ ] given_review_DELETE_when_요청_then_soft_delete
- [ ] given_comment_root_POST_when_생성_then_depth0_parentNull
- [ ] given_comment_reply_POST_when_생성_then_depth1_parent설정
- [ ] given_comment_depth2_POST_when_시도_then_400
- [ ] given_comment_필터_entityType_when_요청_then_적용
- [ ] given_comment_필터_languageCode_생략_when_요청_then_EN기본
- [ ] given_comment_PATCH_update_when_요청_then_내용변경
- [ ] given_comment_DELETE_when_요청_then_soft_delete
- [ ] given_comment_vote_LIKE_POST_when_생성_then_성공
- [ ] given_comment_vote_POST_중복_then_409
- [ ] given_comment_vote_DELETE_when_요청_then_멱등
- [ ] given_comment_vote_필터_entityCommentId_when_요청_then_적용
- [ ] given_vial_POST_when_생성_then_isBlind_false기본
- [ ] given_vial_필터_releaseId_when_요청_then_적용
- [ ] given_vial_필터_isBlind_when_요청_then_적용
- [ ] given_vial_PATCH_completed_true_when_요청_then_completed갱신
- [ ] given_vial_DELETE_when_요청_then_soft_delete
- [ ] given_vial_transfer_POST_when_생성_then_status_ACTIVE
- [ ] given_vial_transfer_PATCH_status_CLAIMED_when_요청_then_transferedAt세팅
- [ ] given_vial_transfer_PATCH_status_잘못된전이_then_400
- [ ] given_vial_transfer_필터_status_when_요청_then_적용
- [ ] given_vial_transfer_token_POST_when_생성_then_claimToken발급
- [ ] given_vial_transfer_token_필터_claimType_when_요청_then_적용
- [ ] given_vial_transfer_token_DELETE_when_요청_then_멱등

### 7.10 Users / Profiles / Social / Roles / Follows / Blocks
- [ ] given_user_POST_when_생성_then_verified_false_userStatus_ACTIVE
- [ ] given_user_필터_status_when_요청_then_적용
- [ ] given_user_필터_verified_when_요청_then_적용
- [ ] given_user_필터_role_when_요청_then_적용
- [ ] given_user_필터_username_부분일치_when_요청_then_포함
- [ ] given_user_PATCH_privacy변경_when_요청_then_갱신
- [ ] given_user_DELETE_when_요청_then_soft_delete
- [ ] given_user_profile_POST_필수누락_then_400
- [ ] given_user_profile_POST_when_생성_then_gender_UNSPECIFIED기본
- [ ] given_user_profile_PUT_full_update_when_요청_then_전체치환
- [ ] given_user_profile_PATCH_partial_update_when_요청_then_부분갱신
- [ ] given_user_profile_DELETE_when_요청_then_soft_delete
- [ ] given_user_social_account_POST_when_생성_then_성공
- [ ] given_user_social_account_POST_중복_provider_providerId_then_409
- [ ] given_user_social_account_DELETE_when_요청_then_멱등
- [ ] given_role_POST_when_생성_then_roleName_유니크저장
- [ ] given_role_POST_중복_then_409
- [ ] given_role_PUT_full_update_when_요청_then_이름변경
- [ ] given_role_DELETE_when_요청_then_삭제
- [ ] given_user_role_POST_when_생성_then_매핑생성
- [ ] given_user_role_POST_중복_then_409
- [ ] given_user_role_DELETE_when_요청_then_멱등
- [ ] given_follow_POST_when_생성_then_status_PENDING
- [ ] given_follow_PATCH_APPROVE_when_요청_then_status_APPROVED
- [ ] given_follow_PATCH_REJECT_when_요청_then_status_REJECTED
- [ ] given_follow_POST_self_follow_then_400
- [ ] given_follow_DELETE_when_요청_then_멱등
- [ ] given_block_POST_when_생성_then_차단저장
- [ ] given_block_POST_self_block_then_400
- [ ] given_block_DELETE_when_요청_then_멱등

### 7.11 Reports & Status Transitions
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

---
## 8. Service / Unit Test Checklist (Tagged)
(아래는 test_checklist2.md 내용을 통합; 우선 P1 먼저 구현.)

### 8.0 Common Utilities
- [ ] getCurrentUserId_인증없음_then_예외  [P1|SEC,EDGE]
- [ ] getCurrentUserId_인증있음_then_정상반환  [P1|SEC]
- [ ] hasRole_ADMIN_권한없음_then_false  [P2|SEC]
- [ ] hasRole_ADMIN_권한있음_then_true  [P2|SEC]
- [ ] assemble_기본값_page_size_null_then_page0_size50  [P1|SPEC]
- [ ] assemble_size_최대초과_then_ValidationException  [P1|VAL]
- [ ] assemble_sort_미지정_then_default_id_ASC  [P2|SPEC]
- [ ] generate_연속호출_then_중복없음  [P2|INV]
- [ ] generate_길이및포맷_then_UUIDv4_패턴  [P2|VAL]
- [ ] validate_올바른값_then_성공  [P2|VAL]
- [ ] validate_잘못된값_then_ValidationException  [P1|VAL]
- [ ] build_기본_then_excludes_deleted  [P1|SPEC]
- [ ] build_includeDeleted_true_then_includes_deleted  [P2|SPEC]
- [ ] approve_IN_REVIEW_then_CONFIRMED  [P1|TR]
- [ ] reject_IN_REVIEW_then_REJECTED  [P1|TR]
- [ ] view_SUPPRESSED_일반사용자_then_accessDenied  [P1|SEC,TR]
- [ ] approve_ALREADY_CONFIRMED_then_idempotent  [P2|TR,IDEM]

### 8.1 CompaniesService
- [ ] create_valid_then_status_IN_REVIEW_default_fields  [P1|CRUD,TR]
- [ ] create_중복이름_then_DuplicateException  [P1|CRUD,VAL]
- [ ] get_public_CONFIRMED_only_then_IN_REVIEW_비노출  [P1|READ,FIL,SEC]
- [ ] patch_null필드포함_then_null_덮어쓰기  [P2|UPDATE]
- [ ] patch_권한없음_then_AccessDenied  [P1|SEC,UPDATE]
- [ ] delete_first_then_softDelete_timestamp  [P1|DELETE]
- [ ] delete_이미_deleted_then_idempotent_no_error  [P2|IDEM,DELETE]
- [ ] get_soft_deleted_then_NotFound  [P1|READ,EDGE]
- [ ] revision_approve_then_mainEntity_status_CONFIRMED  [P1|TR]
- [ ] revision_reject_then_mainEntity_status_변경없음  [P2|TR]

### 8.2 DistilleriesService
- [ ] create_valid_then_status_IN_REVIEW  [P1|CRUD,TR]
- [ ] filter_company_country_city_operationalStatus_AND  [P1|FIL]
- [ ] search_q_case_insensitive  [P1|FIL]
- [ ] patch_권한없음_then_AccessDenied  [P1|SEC,UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]

### 8.3 BrandsService
- [ ] create_valid_then_IN_REVIEW  [P1|CRUD,TR]
- [ ] filter_companyId_only해당  [P1|FIL]
- [ ] search_q_partial  [P1|FIL]
- [ ] patch_null필드_then_null_overwrite  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]

### 8.4 CollectionsService
- [ ] create_valid_then_IN_REVIEW  [P2|CRUD,TR]
- [ ] filter_brandId  [P2|FIL]
- [ ] delete_then_softDelete  [P2|DELETE]

### 8.5 ModelsService
- [ ] create_valid_then_IN_REVIEW  [P2|CRUD,TR]
- [ ] filter_collectionId_and_categoryId_AND  [P2|FIL]
- [ ] delete_then_softDelete  [P2|DELETE]

### 8.6 ReleasesService
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

### 8.7 DistilleryBrandRelationService
- [ ] create_first_then_success_createdBy세팅  [P1|CRUD,REF]
- [ ] create_중복_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_first_then_softDelete  [P1|DELETE]
- [ ] delete_재삭제_then_idempotent  [P2|IDEM,DELETE]
- [ ] filter_distilleryId_applied  [P2|FIL]
- [ ] filter_brandId_applied  [P2|FIL]

### 8.8 ReleaseDistilleryRelationService
- [ ] create_valid_then_success  [P1|CRUD,REF]
- [ ] create_중복_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_first_then_softDelete  [P1|DELETE]
- [ ] delete_재삭제_then_idempotent  [P2|IDEM,DELETE]
- [ ] filter_releaseId  [P2|FIL]
- [ ] filter_distilleryId  [P2|FIL]

### 8.9 EntityRevisionService
- [ ] create_revision_first_then_IN_REVIEW_isLatest_true  [P1|CRUD,TR,INV]
- [ ] create_revision_sameEntity_second_then_previous_isLatest_false  [P1|CRUD,TR,INV]
- [ ] approve_firstRevision_then_mainEntity_publish  [P1|TR]
- [ ] approve_alreadyApproved_then_idempotent  [P2|TR,IDEM]
- [ ] reject_revision_then_status_REJECTED_reviewedAt  [P1|TR]
- [ ] approve_권한없음_then_AccessDenied  [P1|SEC,TR]
- [ ] filter_entityType  [P2|FIL]

### 8.10 EntityTagService
- [ ] create_unique_then_ok  [P1|CRUD,VAL]
- [ ] create_duplicate_sameEntity_lang_tag_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_first_then_softDelete  [P1|DELETE]
- [ ] delete_재삭제_then_idempotent  [P2|IDEM,DELETE]
- [ ] filter_entityType  [P2|FIL]
- [ ] filter_languageCode  [P2|FIL]

### 8.11 CategoryService
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

### 8.12 GeoReadOnlyService
- [ ] create_attempt_then_UnsupportedOperation  [P1|EDGE]
- [ ] update_attempt_then_UnsupportedOperation  [P1|EDGE]
- [ ] delete_attempt_then_UnsupportedOperation  [P1|EDGE]
- [ ] get_notFound_then_NotFound  [P1|READ,EDGE]
- [ ] list_regions_then_ok  [P2|READ]

### 8.13 ShelfService & ShelfShareTokenService
- [ ] create_valid_then_privacy_PUBLIC_default  [P1|CRUD,SEC]
- [ ] filter_ownerId_onlyOwner  [P1|FIL,SEC]
- [ ] filter_privacy_PRIVATE_타사용자_then_제외  [P1|FIL,SEC]
- [ ] put_full_replace  [P2|UPDATE]
- [ ] patch_partial_update  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]
- [ ] get_soft_deleted_then_NotFound  [P1|READ,EDGE]
- [ ] create_then_claimToken_발급_만료시간세팅  [P1|CRUD,MAP]
- [ ] patch_status_valid_transition_then_ok  [P1|TR]
- [ ] patch_status_invalid_transition_then_ValidationException  [P1|TR,VAL]
- [ ] delete_then_invalidate  [P1|DELETE,IDEM]
- [ ] filter_shelfId  [P2|FIL]
- [ ] filter_status  [P2|FIL]

### 8.14 ReviewService
- [ ] create_minimal_then_defaults_applied  [P1|CRUD,MAP]
- [ ] filter_releaseId  [P1|FIL]
- [ ] filter_privacy_PUBLIC_타사용자_then_포함  [P1|FIL,SEC]
- [ ] filter_privacy_PRIVATE_타사용자_then_제외  [P1|FIL,SEC]
- [ ] create_score_100초과_then_ValidationException  [P1|VAL]
- [ ] create_score_음수_then_ValidationException  [P1|VAL]
- [ ] put_full_replace  [P2|UPDATE]
- [ ] patch_partial_update  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]

### 8.15 CommentService & CommentVoteService
- [ ] create_root_then_depth0_parentNull  [P1|CRUD,MAP]
- [ ] create_reply_then_depth1_parent세팅  [P1|CRUD,MAP]
- [ ] create_depth2_then_ValidationException  [P1|CRUD,VAL]
- [ ] filter_entityType  [P2|FIL]
- [ ] filter_languageCode_none_then_default_EN  [P2|FIL,MAP]
- [ ] patch_update_content  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]
- [ ] create_like_first_then_success  [P1|CRUD]
- [ ] create_duplicate_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_then_idempotent  [P2|DELETE,IDEM]
- [ ] filter_entityCommentId  [P2|FIL]

### 8.16 Vial / Transfer / Token Services
- [ ] create_valid_then_isBlind_false_default  [P1|CRUD,MAP]
- [ ] filter_releaseId  [P2|FIL]
- [ ] filter_isBlind  [P2|FIL]
- [ ] patch_completed_true_then_fieldUpdated  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]
- [ ] create_valid_then_status_ACTIVE  [P1|CRUD,TR]
- [ ] patch_status_CLAIMED_valid_then_transferedAt_set  [P1|TR,MAP]
- [ ] patch_status_invalid_transition_then_ValidationException  [P1|TR,VAL]
- [ ] filter_status  [P2|FIL]
- [ ] create_then_claimToken_generated  [P1|CRUD,MAP]
- [ ] filter_claimType  [P2|FIL]
- [ ] delete_then_idempotent  [P2|DELETE,IDEM]

### 8.17 User / Profile / Social / Role / Mapping Services
- [ ] create_valid_then_verified_false_userStatus_ACTIVE_defaults  [P1|CRUD,MAP]
- [ ] filter_status  [P1|FIL]
- [ ] filter_verified  [P1|FIL]
- [ ] filter_role  [P1|FIL,SEC]
- [ ] filter_username_partial_case_insensitive  [P2|FIL]
- [ ] patch_privacy_then_updated  [P2|UPDATE,SEC]
- [ ] delete_then_softDelete  [P1|DELETE]
- [ ] create_missing_required_then_ValidationException  [P1|CRUD,VAL]
- [ ] create_valid_then_gender_UNSPECIFIED_default  [P1|CRUD,MAP]
- [ ] put_full_replace  [P2|UPDATE]
- [ ] patch_partial_update  [P2|UPDATE]
- [ ] delete_then_softDelete  [P1|DELETE]
- [ ] create_unique_provider_providerId_then_ok  [P1|CRUD,VAL]
- [ ] create_duplicate_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_then_idempotent  [P2|DELETE,IDEM]
- [ ] create_unique_roleName_then_ok  [P1|CRUD,VAL]
- [ ] create_duplicate_then_DuplicateException  [P1|CRUD,VAL]
- [ ] put_full_update_name_changed  [P2|UPDATE]
- [ ] delete_then_removed  [P1|DELETE]
- [ ] create_mapping_first_then_ok  [P1|CRUD,REF]
- [ ] create_mapping_duplicate_then_DuplicateException  [P1|CRUD,VAL]
- [ ] delete_mapping_then_idempotent  [P2|DELETE,IDEM]
- [ ] create_follow_then_status_PENDING  [P1|CRUD,TR]
- [ ] approve_pending_then_APPROVED  [P1|TR]
- [ ] reject_pending_then_REJECTED  [P1|TR]
- [ ] create_self_follow_then_ValidationException  [P1|VAL]
- [ ] delete_then_idempotent  [P2|DELETE,IDEM]
- [ ] create_block_then_success  [P1|CRUD]
- [ ] create_self_block_then_ValidationException  [P1|VAL]
- [ ] delete_then_idempotent  [P2|DELETE,IDEM]

### 8.18 Report Services (User / Content)
- [ ] create_user_report_then_status_PENDING  [P1|CRUD,TR]
- [ ] filter_user_reporterId  [P2|FIL]
- [ ] filter_user_status  [P2|FIL]
- [ ] patch_user_report_status_transition_valid  [P1|TR]
- [ ] patch_user_report_status_invalid_then_ValidationException  [P1|TR,VAL]
- [ ] create_content_report_then_status_PENDING  [P1|CRUD,TR]
- [ ] filter_content_targetType  [P2|FIL]
- [ ] patch_content_report_status_transition_valid  [P1|TR]
- [ ] patch_content_report_status_invalid_then_ValidationException  [P1|TR,VAL]

### 8.19 Cross / Edge / Concurrency
- [ ] patch_unknown_entity_then_NotFound  [P1|EDGE,UPDATE]
- [ ] get_deleted_entity_without_includeDeleted_then_NotFound  [P1|EDGE,READ]
- [ ] list_includeDeleted_false_then_no_deleted  [P1|READ,INV]
- [ ] softDelete_concurrent_second_call_then_idempotent  [P1|CONC,IDEM]
- [ ] approve_revision_race_condition_lastWriteWins_via_versionCheck  [P1|CONC,TR]
- [ ] repository_exception_on_save_then_wrapped_DomainException  [P1|EDGE]
- [ ] optimistic_lock_version_mismatch_then_OptimisticLockException  [P2|CONC,UPDATE]

### 8.20 Extensions (Backlog)
- [ ] approve_revision_when_cacheEnabled_then_cacheInvalidated  [P3|CACHE,TR,OPT]
- [ ] restore_soft_deleted_entity_then_restored_fields  [P3|CRUD,OPT]
- [ ] batch_import_validation_then_partial_fail_strategy  [P3|CRUD,VAL,OPT]

---
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
