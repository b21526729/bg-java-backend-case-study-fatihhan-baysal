package com.example.bilisimgarajitask.teacher;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TeacherClassroomAssignRequest(
        @NotNull UUID teacherId,
        @NotNull UUID classroomId,
        Boolean active
) {}
