package com.example.bilisimgarajitask.classroom;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService service;

    @PostMapping
    public ResponseEntity<ClassroomResponse> create(@Valid @RequestBody ClassroomCreateRequest req) {
        ClassroomResponse res = service.create(req);
        return ResponseEntity.created(URI.create("/api/v1/classrooms/" + res.id())).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<ClassroomResponse>> list(@RequestParam(required = false) UUID organizationId) {
        return ResponseEntity.ok(service.list(organizationId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassroomResponse> update(@PathVariable UUID id,
                                                    @Valid @RequestBody ClassroomUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
