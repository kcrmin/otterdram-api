package com.otterdram.otterdram.domain.spirits.company.repository;

import com.otterdram.otterdram.domain.spirits.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByCompanyName(String companyName);
}
