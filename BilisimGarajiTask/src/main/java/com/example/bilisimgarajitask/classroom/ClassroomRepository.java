package com.example.bilisimgarajitask.classroom;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    boolean existsByOrganizationIdAndNameIgnoreCase(UUID organizationId, String name);
    List<Classroom> findAllByOrganizationId(UUID organizationId, Sort sort);

    long countByOrganizationId(UUID organizationId);

}
