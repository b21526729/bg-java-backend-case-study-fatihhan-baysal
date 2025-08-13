package com.example.bilisimgarajitask.brand;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI; import java.util.*;

@PreAuthorize("hasRole('SUPER_ADMIN')")
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class BrandController {
    private final BrandService service;



    @PostMapping
    public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandCreateRequest req){
        BrandResponse res=service.create(req);
        return ResponseEntity.created(URI.create("/api/v1/brands/"+res.id())).body(res);
    }
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(service.get(id));
    }
    @GetMapping
    public ResponseEntity<List<BrandResponse>> list(){
        return ResponseEntity.ok(service.list());
    }
    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> update(@PathVariable UUID id, @Valid @RequestBody BrandUpdateRequest req){
        return ResponseEntity.ok(service.update(id, req));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        service.delete(id); return ResponseEntity.noContent().build();
    }
}