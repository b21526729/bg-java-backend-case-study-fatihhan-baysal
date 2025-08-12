package com.example.bilisimgarajitask.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseUpdateRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 500) String description,
        Boolean active
) {}
