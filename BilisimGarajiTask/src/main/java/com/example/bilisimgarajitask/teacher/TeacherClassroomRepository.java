package com.example.bilisimgarajitask.teacher;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeacherClassroomRepository extends JpaRepository<TeacherClassroom, UUID> {
    boolean existsByTeacherIdAndClassroomId(UUID teacherId, UUID classroomId);
    Optional<TeacherClassroom> findByTeacherIdAndClassroomId(UUID teacherId, UUID classroomId);

    List<TeacherClassroom> findAllByTeacherId(UUID teacherId, Sort sort);
    List<TeacherClassroom> findAllByClassroomId(UUID classroomId, Sort sort);

    long countByClassroomId(UUID classroomId);

    long countByTeacherId(UUID teacherId);
}
