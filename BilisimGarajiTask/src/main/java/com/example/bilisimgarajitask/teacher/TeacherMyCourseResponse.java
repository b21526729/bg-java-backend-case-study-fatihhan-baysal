package com.example.bilisimgarajitask.teacher;

import java.util.UUID;

public record TeacherMyCourseResponse(
        UUID classroomId,
        String classroomName,
        UUID organizationId,
        String organizationName,
        UUID courseId,
        String courseName,
        String courseCode,
        boolean active
) {}
