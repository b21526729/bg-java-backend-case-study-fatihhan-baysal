package com.example.bilisimgarajitask.classroomcourse;

import java.time.Instant;
import java.util.UUID;

public record ClassroomCourseResponse(
        UUID id,
        UUID classroomId,
        String classroomName,
        UUID organizationId,
        String organizationName,
        UUID courseId,
        String courseName,
        String courseCode,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
