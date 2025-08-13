package com.example.bilisimgarajitask.teacher;

import com.example.bilisimgarajitask.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@PreAuthorize("hasRole('TEACHER')")
@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class TeacherMeController {

    private final TeacherReadService service;

    @GetMapping("/my-classes")
    public ResponseEntity<List<TeacherMyClassResponse>> myClasses() {
        String email = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new AccessDeniedException("Authentication required"));
        return ResponseEntity.ok(
                service.listMyClassesByEmail(email).stream()
                        .sorted(Comparator.comparing(TeacherMyClassResponse::classroomName, String.CASE_INSENSITIVE_ORDER))
                        .toList()
        );
    }

    @GetMapping("/my-students")
    public ResponseEntity<List<TeacherMyStudentResponse>> myStudents() {
        String email = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new AccessDeniedException("Authentication required"));
        return ResponseEntity.ok(
                service.listMyStudentsByEmail(email).stream()
                        .sorted(Comparator
                                .comparing(TeacherMyStudentResponse::lastName, String.CASE_INSENSITIVE_ORDER)
                                .thenComparing(TeacherMyStudentResponse::firstName, String.CASE_INSENSITIVE_ORDER))
                        .toList()
        );
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<TeacherMyCourseResponse>> myCourses() {
        String email = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new AccessDeniedException("Authentication required"));
        return ResponseEntity.ok(
                service.listMyCoursesByEmail(email).stream()
                        .sorted(Comparator
                                .comparing(TeacherMyCourseResponse::classroomName, String.CASE_INSENSITIVE_ORDER)
                                .thenComparing(TeacherMyCourseResponse::courseName, String.CASE_INSENSITIVE_ORDER))
                        .toList()
        );
    }
}
