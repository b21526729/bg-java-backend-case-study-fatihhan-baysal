package com.example.bilisimgarajitask.classroomcourse;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ClassroomCourseAssignRequest(
        @NotNull UUID classroomId,
        @NotNull UUID courseId,
        Boolean active
) {}
