package com.example.bilisimgarajitask.auth;

import com.example.bilisimgarajitask.user.Role;
import java.util.UUID;

public record MeResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Role role,
        Integer profileId,
        UUID organizationId,
        String organizationName,
        UUID classroomId,
        String classroomName
) {}
