package com.example.bilisimgarajitask.classroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ClassroomCreateRequest(
        @NotNull UUID organizationId,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 16) String grade,
        @Size(max = 16) String section,
        Boolean active
) {}
