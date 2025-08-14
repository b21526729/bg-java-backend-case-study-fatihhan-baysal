package com.example.bilisimgarajitask.teacher;

final class TeacherClassroomMapper {
    private TeacherClassroomMapper() {}

    static TeacherClassroomResponse toResponse(TeacherClassroom tc) {
        String tName = tc.getTeacher().getFirstName() + " " + tc.getTeacher().getLastName();
        String cName = tc.getClassroom().getName();
        return new TeacherClassroomResponse(
                tc.getId(),
                tc.getTeacher().getId(),
                tName,
                tc.getClassroom().getId(),
                cName,
                tc.isActive(),
                tc.getCreatedAt(),
                tc.getUpdatedAt()
        );
    }
}
