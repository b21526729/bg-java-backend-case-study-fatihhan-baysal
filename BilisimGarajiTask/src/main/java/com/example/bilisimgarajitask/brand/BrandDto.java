package com.example.bilisimgarajitask.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

 record BrandCreateRequest(@NotBlank @Size(max=150) String name,
                                 @Size(max=500) String description,
                                 Boolean active) {}
 record BrandUpdateRequest(@NotBlank @Size(max=150) String name,
                                 @Size(max=500) String description,
                                 Boolean active) {}
 record BrandResponse(UUID id, String name, String code, String description,
                            boolean active, Instant createdAt, Instant updatedAt) {}