package com.example.bilisimgarajitask.teacher;

import java.util.UUID;

public record TeacherMyClassResponse(
        UUID classroomId,
        String classroomName,
        String grade,
        String section,
        UUID organizationId,
        String organizationName
) {}
