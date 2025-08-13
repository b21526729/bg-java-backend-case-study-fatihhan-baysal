package com.example.bilisimgarajitask.teacher;

import java.util.UUID;

public record TeacherMyStudentResponse(
        UUID studentId,
        String email,
        String firstName,
        String lastName,
        boolean active,
        UUID classroomId,
        String classroomName,
        UUID organizationId,
        String organizationName
) {}
