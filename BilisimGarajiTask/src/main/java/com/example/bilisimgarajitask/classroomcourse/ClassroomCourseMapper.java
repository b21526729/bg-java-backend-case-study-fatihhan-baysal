package com.example.bilisimgarajitask.classroomcourse;

final class ClassroomCourseMapper {
    private ClassroomCourseMapper(){}

    static ClassroomCourseResponse toResponse(ClassroomCourse cc){
        var cls = cc.getClassroom();
        var org = cls.getOrganization();
        var crs = cc.getCourse();
        return new ClassroomCourseResponse(
                cc.getId(),
                cls.getId(),
                cls.getName(),
                org.getId(),
                org.getName(),
                crs.getId(),
                crs.getName(),
                crs.getCode(),
                cc.isActive(),
                cc.getCreatedAt(),
                cc.getUpdatedAt()
        );
    }
}
