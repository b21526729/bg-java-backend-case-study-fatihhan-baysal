package com.example.bilisimgarajitask.classroom;

final class ClassroomMapper {
    private ClassroomMapper() {}

    static ClassroomResponse toResponse(Classroom c) {
        return new ClassroomResponse(
                c.getId(),
                c.getName(),
                c.getGrade(),
                c.getSection(),
                c.isActive(),
                c.getOrganization().getId(),
                c.getOrganization().getName(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }

    static void applyUpdate(Classroom c, ClassroomUpdateRequest r) {
        c.setName(r.name());
        c.setGrade(r.grade());
        c.setSection(r.section());
        if (r.active() != null) c.setActive(r.active());
    }
}
