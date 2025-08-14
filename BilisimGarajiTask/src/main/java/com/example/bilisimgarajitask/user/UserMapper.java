package com.example.bilisimgarajitask.user;

final class UserMapper {
    private UserMapper(){}

    static UserResponse toResponse(User u){
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getRole(),
                u.getProfileId(),
                u.getOrganization() != null ? u.getOrganization().getId() : null,
                u.getOrganization() != null ? u.getOrganization().getName() : null,
                u.getClassroom() != null ? u.getClassroom().getId() : null,
                u.getClassroom() != null ? u.getClassroom().getName() : null,
                u.isActive(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }

    static void applyUpdate(User u, UserUpdateRequest r){
        if (r.firstName() != null) u.setFirstName(r.firstName());
        if (r.lastName()  != null) u.setLastName(r.lastName());
        if (r.active()    != null) u.setActive(r.active());

    }
}
