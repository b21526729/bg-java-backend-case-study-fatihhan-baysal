package com.example.bilisimgarajitask.course;

import java.time.Instant;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String name,
        String code,
        String description,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
