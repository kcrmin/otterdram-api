package com.otterdram.otterdram.testsupport;

import com.otterdram.otterdram.domain.spirits.company.Company;
import com.otterdram.otterdram.domain.spirits.company.repository.CompanyRepository;
import com.otterdram.otterdram.domain.spirits.revision.EntityRevision;
import com.otterdram.otterdram.domain.spirits.revision.repository.RevisionRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;

public final class RepositoryStubs {
    private RepositoryStubs() {}

    public static void noDuplicateName(CompanyRepository repo, String companyName) {
        when(repo.existsByCompanyName(companyName)).thenReturn(false);
    }

    public static void duplicateName(CompanyRepository repo, String companyName, Company existing) {
        when(repo.existsByCompanyName(companyName)).thenReturn(true);
    }

    public static void existingCompanyById(CompanyRepository repo, Company existing) {
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));
    }

    public static void existingCompanyReferenceById(CompanyRepository repo, Company existing) {
        when(repo.getReferenceById(existing.getId())).thenReturn(existing);
    }

    public static void existingPendingRevision(RevisionRepository repo, EntityRevision existing) {
        when(repo.findByEntityTypeAndEntityIdAndStatus(
                existing.getEntityType(),
                existing.getEntityId(),
                existing.getStatus()
        )).thenReturn(Optional.of(existing));
    }

    public static void existingRevisionById(RevisionRepository repo, EntityRevision existing) {
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));
    }

    public static void saveCompany(CompanyRepository repo, Company toSave, Company toReturn) {
        when(repo.save(toSave)).thenReturn(toReturn);
    }

    public static void saveRevision(RevisionRepository repo, EntityRevision toSave, EntityRevision toReturn) {
        when(repo.save(toSave)).thenReturn(toReturn);
    }
}
