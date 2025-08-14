package com.example.bilisimgarajitask.student;

import com.example.bilisimgarajitask.classroom.Classroom;
import com.example.bilisimgarajitask.classroomcourse.ClassroomCourseRepository;
import com.example.bilisimgarajitask.common.NotFoundException;
import com.example.bilisimgarajitask.user.Role;
import com.example.bilisimgarajitask.user.User;
import com.example.bilisimgarajitask.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentReadService {

    private final UserRepository userRepo;
    private final ClassroomCourseRepository classroomCourseRepo;

    public List<StudentMyCourseResponse> listMyCoursesByEmail(String email){
        User student = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User not found by email: " + email));
        if (student.getRole() != Role.STUDENT) {
            throw new AccessDeniedException("Only STUDENT can access /my-courses");
        }
        return listMyCoursesByStudentId(student.getId());
    }
    @Cacheable(cacheNames = "std:myCourses", key = "#studentId")
    public List<StudentMyCourseResponse> listMyCoursesByStudentId(UUID studentId){
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new NotFoundException("User not found: " + studentId));
        if (student.getRole() != Role.STUDENT) {
            throw new AccessDeniedException("Only STUDENT can access /my-courses");
        }
        Classroom c = student.getClassroom();
        if (c == null) {
            throw new ResponseStatusException(BAD_REQUEST,"Student has no classroom assignment");
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "course.name");
        return classroomCourseRepo.findAllByClassroomId(c.getId(), sort).stream()
                .map(cc -> new StudentMyCourseResponse(
                        cc.getCourse().getId(),
                        cc.getCourse().getName(),
                        cc.getCourse().getCode(),
                        c.getId(),
                        c.getName(),
                        c.getOrganization().getId(),
                        c.getOrganization().getName(),
                        cc.isActive()
                ))
                .toList();
    }
}
