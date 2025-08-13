package com.example.bilisimgarajitask.teacher;

import com.example.bilisimgarajitask.classroom.Classroom;
import com.example.bilisimgarajitask.classroomcourse.ClassroomCourse;
import com.example.bilisimgarajitask.classroomcourse.ClassroomCourseRepository;
import com.example.bilisimgarajitask.common.NotFoundException;
import com.example.bilisimgarajitask.user.Role;
import com.example.bilisimgarajitask.user.User;
import com.example.bilisimgarajitask.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherReadService {

    private final TeacherClassroomRepository tcRepo;
    private final UserRepository userRepo;
    private final ClassroomCourseRepository classroomCourseRepo;

    public List<TeacherMyClassResponse> listMyClassesByEmail(String email){
        User teacher = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User not found by email: " + email));
        if (teacher.getRole() != Role.TEACHER) {
            throw new AccessDeniedException("Only TEACHER can access /my-classes");
        }
        return listMyClassesByTeacherId(teacher.getId());
    }

    public List<TeacherMyClassResponse> listMyClassesByTeacherId(UUID teacherId){
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return tcRepo.findAllByTeacherId(teacherId, sort).stream()
                .map(tc -> {
                    Classroom c = tc.getClassroom();
                    return new TeacherMyClassResponse(
                            c.getId(),
                            c.getName(),
                            c.getGrade(),
                            c.getSection(),
                            c.getOrganization().getId(),
                            c.getOrganization().getName()
                    );
                })
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(TeacherMyClassResponse::classroomId, r -> r, (a,b)->a, LinkedHashMap::new),
                        m -> new ArrayList<>(m.values())
                ));
    }

    public List<TeacherMyStudentResponse> listMyStudentsByEmail(String email){
        User teacher = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User not found by email: " + email));
        if (teacher.getRole() != Role.TEACHER) {
            throw new AccessDeniedException("Only TEACHER can access /my-students");
        }
        return listMyStudentsByTeacherId(teacher.getId());
    }

    public List<TeacherMyStudentResponse> listMyStudentsByTeacherId(UUID teacherId){
        // Öğretmenin atandığı sınıflar
        var sortAssign = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Classroom> classrooms = tcRepo.findAllByTeacherId(teacherId, sortAssign).stream()
                .map(TeacherClassroom::getClassroom)
                .distinct()
                .toList();

        if (classrooms.isEmpty()) return List.of();

        List<UUID> classroomIds = classrooms.stream().map(Classroom::getId).toList();

        // Bu sınıflardaki öğrenciler
        var sortStudents = Sort.by(Sort.Direction.ASC, "firstName")
                .and(Sort.by(Sort.Direction.ASC, "lastName"));
        return userRepo.findAllByRoleAndClassroomIdIn(Role.STUDENT, classroomIds, sortStudents).stream()
                .map(s -> new TeacherMyStudentResponse(
                        s.getId(),
                        s.getEmail(),
                        s.getFirstName(),
                        s.getLastName(),
                        s.isActive(),
                        s.getClassroom() != null ? s.getClassroom().getId() : null,
                        s.getClassroom() != null ? s.getClassroom().getName() : null,
                        s.getOrganization() != null ? s.getOrganization().getId() : null,
                        s.getOrganization() != null ? s.getOrganization().getName() : null
                ))
                .collect(Collectors.toList());
    }


    public List<TeacherMyCourseResponse> listMyCoursesByEmail(String email){
        User teacher = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User not found by email: " + email));
        if (teacher.getRole() != Role.TEACHER) {
            throw new AccessDeniedException("Only TEACHER can access /my-courses");
        }
        return listMyCoursesByTeacherId(teacher.getId());
    }

    public List<TeacherMyCourseResponse> listMyCoursesByTeacherId(UUID teacherId){
        // Öğretmenin atandığı sınıfları topla
        var sortAssign = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Classroom> classrooms = tcRepo.findAllByTeacherId(teacherId, sortAssign).stream()
                .map(TeacherClassroom::getClassroom)
                .distinct()
                .toList();
        if (classrooms.isEmpty()) return List.of();

        List<UUID> clsIds = classrooms.stream().map(Classroom::getId).toList();

        // Bu sınıflara atanmış dersleri çek
        var sortCc = Sort.by(Sort.Direction.ASC, "classroom.name")
                .and(Sort.by(Sort.Direction.ASC, "course.name"));
        return classroomCourseRepo.findAllByClassroomIdIn(clsIds, sortCc).stream()
                .map(cc -> new TeacherMyCourseResponse(
                        cc.getClassroom().getId(),
                        cc.getClassroom().getName(),
                        cc.getClassroom().getOrganization().getId(),
                        cc.getClassroom().getOrganization().getName(),
                        cc.getCourse().getId(),
                        cc.getCourse().getName(),
                        cc.getCourse().getCode(),
                        cc.isActive()
                ))
                .collect(Collectors.toList());
    }
}
