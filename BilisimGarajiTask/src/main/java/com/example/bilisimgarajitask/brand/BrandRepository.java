package com.example.bilisimgarajitask.brand;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
    boolean existsByCode(String code);
    Optional<Brand> findByCode(String code);
}
