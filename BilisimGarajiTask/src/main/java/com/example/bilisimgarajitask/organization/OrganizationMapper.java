package com.example.bilisimgarajitask.organization;

final class OrganizationMapper {
    private OrganizationMapper() {}

    static OrganizationResponse toResponse(Organization o) {
        return new OrganizationResponse(
                o.getId(),
                o.getName(),
                o.getCode(),
                o.getDescription(),
                o.isActive(),
                o.getBrand().getId(),
                o.getBrand().getName(),
                o.getCreatedAt(),
                o.getUpdatedAt()
        );
    }

    static void applyUpdate(Organization o, OrganizationUpdateRequest r) {
        o.setName(r.name());
        o.setDescription(r.description());
        if (r.active() != null) o.setActive(r.active());
    }
}
