package com.example.bilisimgarajitask.student;

import java.util.UUID;

public record StudentMyCourseResponse(
        UUID courseId,
        String courseName,
        String courseCode,
        UUID classroomId,
        String classroomName,
        UUID organizationId,
        String organizationName,
        boolean active // assignment aktifliği
) {}
