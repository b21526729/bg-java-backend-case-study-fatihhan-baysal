package com.example.bilisimgarajitask.organization;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String code,
        String description,
        boolean active,
        UUID brandId,
        String brandName,
        Instant createdAt,
        Instant updatedAt
) {}
