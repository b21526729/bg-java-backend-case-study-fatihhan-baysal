package com.example.bilisimgarajitask.classroomcourse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@PreAuthorize("hasRole('SUPER_ADMIN')") // RBAC kilidini açtıysan kullan
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class ClassroomCourseController {

    private final ClassroomCourseService service;

    // POST /api/v1/courses/assign
    @PostMapping("/assign")
    public ResponseEntity<ClassroomCourseResponse> assign(@Valid @RequestBody ClassroomCourseAssignRequest req){
        return ResponseEntity.ok(service.assign(req));
    }

    // GET /api/v1/courses/assignments?classroomId=...&courseId=...
    @GetMapping("/assignments")
    public ResponseEntity<List<ClassroomCourseResponse>> list(
            @RequestParam(required = false) UUID classroomId,
            @RequestParam(required = false) UUID courseId){
        return ResponseEntity.ok(service.list(classroomId, courseId));
    }

    // DELETE /api/v1/courses/assign?classroomId=...&courseId=...
    @DeleteMapping("/assign")
    public ResponseEntity<Void> unassign(@RequestParam UUID classroomId,
                                         @RequestParam UUID courseId){
        service.unassign(classroomId, courseId);
        return ResponseEntity.noContent().build();
    }
}
