package com.example.bilisimgarajitask.classroomcourse;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ClassroomCourseRepository extends JpaRepository<ClassroomCourse, UUID> {
    boolean existsByClassroomIdAndCourseId(UUID classroomId, UUID courseId);
    Optional<ClassroomCourse> findByClassroomIdAndCourseId(UUID classroomId, UUID courseId);

    List<ClassroomCourse> findAllByClassroomId(UUID classroomId, Sort sort);
    List<ClassroomCourse> findAllByCourseId(UUID courseId, Sort sort);

    // ⇩ EKLENDİ: Öğretmenin birden fazla sınıfı için toplu çekim
    List<ClassroomCourse> findAllByClassroomIdIn(Collection<UUID> classroomIds, Sort sort);

    long countByClassroomId(UUID classroomId);

    long countByCourseId(UUID courseId);
}
