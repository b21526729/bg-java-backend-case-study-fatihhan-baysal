package com.example.bilisimgarajitask.student;

import com.example.bilisimgarajitask.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@PreAuthorize("hasRole('STUDENT')")
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class StudentMeController {

    private final StudentReadService service;

    @GetMapping("/my-courses")
    public ResponseEntity<List<StudentMyCourseResponse>> myCourses() {
        String email = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new AccessDeniedException("Authentication required"));
        return ResponseEntity.ok(
                service.listMyCoursesByEmail(email).stream()
                        .sorted(Comparator.comparing(StudentMyCourseResponse::courseName, String.CASE_INSENSITIVE_ORDER))
                        .toList()
        );
    }
}
