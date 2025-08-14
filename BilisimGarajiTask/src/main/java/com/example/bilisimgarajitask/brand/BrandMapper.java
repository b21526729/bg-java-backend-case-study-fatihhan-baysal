package com.example.bilisimgarajitask.brand;

final class BrandMapper {
    private BrandMapper() {}
    static BrandResponse toResponse(Brand b){
        return new BrandResponse(b.getId(), b.getName(), b.getCode(), b.getDescription(),
                b.isActive(), b.getCreatedAt(), b.getUpdatedAt());
    }
    static void applyUpdate(Brand b, BrandUpdateRequest r){
        b.setName(r.name()); b.setDescription(r.description());
        if (r.active()!=null) b.setActive(r.active());
    }
}