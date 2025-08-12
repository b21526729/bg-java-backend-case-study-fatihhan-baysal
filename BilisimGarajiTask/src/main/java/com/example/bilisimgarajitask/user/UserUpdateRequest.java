package com.example.bilisimgarajitask.user;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(max = 80) String firstName,
        @Size(max = 80) String lastName,
        @Size(min = 6, max = 120) String password,
        Boolean active
) {}
