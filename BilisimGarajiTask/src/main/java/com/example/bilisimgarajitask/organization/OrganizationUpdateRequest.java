package com.example.bilisimgarajitask.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizationUpdateRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 500) String description,
        Boolean active
) {}
