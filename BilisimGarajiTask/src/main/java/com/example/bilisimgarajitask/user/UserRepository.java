package com.example.bilisimgarajitask.user;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailIgnoreCase(String email);
    List<User> findAllByRole(Role role, Sort sort);
    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findAllByRoleAndClassroomIdIn(Role role, Collection<UUID> classroomIds, Sort sort);

    boolean existsByRole(Role role);

    long countByOrganizationIdAndRole(UUID organizationId, Role role);
    long countByClassroomIdAndRole(UUID classroomId, Role role);

}
