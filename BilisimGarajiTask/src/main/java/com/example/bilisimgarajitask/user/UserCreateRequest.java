package com.example.bilisimgarajitask.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserCreateRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 1, max = 120) String password,
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotNull Integer profileId,
        UUID organizationId,
        UUID classroomId,
        Boolean active
) {}
