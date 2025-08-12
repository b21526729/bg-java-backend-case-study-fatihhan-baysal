package com.example.bilisimgarajitask.course;

final class CourseMapper {
    private CourseMapper() {}

    static CourseResponse toResponse(Course c) {
        return new CourseResponse(
                c.getId(),
                c.getName(),
                c.getCode(),
                c.getDescription(),
                c.isActive(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }

    static void applyUpdate(Course c, CourseUpdateRequest r) {
        c.setName(r.name());
        c.setDescription(r.description());
        if (r.active() != null) c.setActive(r.active());
    }
}
