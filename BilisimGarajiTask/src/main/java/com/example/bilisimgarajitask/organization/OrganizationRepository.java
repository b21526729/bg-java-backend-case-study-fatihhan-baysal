package com.example.bilisimgarajitask.organization;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    boolean existsByBrandIdAndNameIgnoreCase(UUID brandId, String name);
    boolean existsByBrandIdAndCodeIgnoreCase(UUID brandId, String code);
    List<Organization> findAllByBrandId(UUID brandId, Sort sort);
}
