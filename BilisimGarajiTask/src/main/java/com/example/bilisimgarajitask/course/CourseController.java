package com.example.bilisimgarajitask.course;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    @PostMapping
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseCreateRequest req) {
        CourseResponse res = service.create(req);
        return ResponseEntity.created(URI.create("/api/v1/courses/" + res.id())).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> list(@RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(service.list(active));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody CourseUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
