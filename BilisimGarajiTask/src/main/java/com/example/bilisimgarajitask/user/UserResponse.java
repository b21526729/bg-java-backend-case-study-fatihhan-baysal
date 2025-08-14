package com.example.bilisimgarajitask.user;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Role role,
        Integer profileId,
        UUID organizationId,
        String organizationName,
        UUID classroomId,
        String classroomName,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
