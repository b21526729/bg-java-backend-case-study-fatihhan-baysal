package com.example.bilisimgarajitask.teacher;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('SUPER_ADMIN')")
@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class TeacherClassroomController {

    private final TeacherClassroomService service;

    @PostMapping("/assign-classroom")
    public ResponseEntity<TeacherClassroomResponse> assign(@Valid @RequestBody TeacherClassroomAssignRequest req){
        return ResponseEntity.ok(service.assign(req));
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<TeacherClassroomResponse>> list(
            @RequestParam(required = false) UUID teacherId,
            @RequestParam(required = false) UUID classroomId
    ){
        return ResponseEntity.ok(service.list(teacherId, classroomId));
    }

    @DeleteMapping("/unassign-classroom")
    public ResponseEntity<Void> unassign(@RequestParam UUID teacherId,
                                         @RequestParam UUID classroomId){
        service.unassign(teacherId, classroomId);
        return ResponseEntity.noContent().build();
    }
}
