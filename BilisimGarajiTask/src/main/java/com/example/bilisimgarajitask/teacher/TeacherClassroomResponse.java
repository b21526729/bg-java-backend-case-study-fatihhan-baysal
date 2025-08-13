package com.example.bilisimgarajitask.teacher;

import java.time.Instant;
import java.util.UUID;

public record TeacherClassroomResponse(
        UUID id,
        UUID teacherId,
        String teacherName,
        UUID classroomId,
        String classroomName,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
