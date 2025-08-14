package com.example.bilisimgarajitask.classroom;

import java.time.Instant;
import java.util.UUID;

public record ClassroomResponse(
        UUID id,
        String name,
        String grade,
        String section,
        boolean active,
        UUID organizationId,
        String organizationName,
        Instant createdAt,
        Instant updatedAt
) {}
