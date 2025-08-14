package com.example.bilisimgarajitask.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record OrganizationCreateRequest(
        @NotNull UUID brandId,
        @NotBlank @Size(max = 150) String name,
        @Size(max = 500) String description,
        Boolean active
) {}
