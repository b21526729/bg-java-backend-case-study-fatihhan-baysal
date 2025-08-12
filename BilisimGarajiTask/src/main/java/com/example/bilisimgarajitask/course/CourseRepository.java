package com.example.bilisimgarajitask.course;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    boolean existsByCodeIgnoreCase(String code);
    List<Course> findAllByActive(boolean active, Sort sort);
}
