package com.example.bilisimgarajitask.organization;

import com.example.bilisimgarajitask.brand.Brand;
import com.example.bilisimgarajitask.brand.BrandRepository;
import com.example.bilisimgarajitask.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final BrandRepository brandRepository;

    private static final String PREFIX = "ORG-";
    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Random rnd = new Random();

    public OrganizationResponse create(OrganizationCreateRequest req) {
        Brand brand = brandRepository.findById(req.brandId())
                .orElseThrow(() -> new NotFoundException("Brand not found: " + req.brandId()));

        if (organizationRepository.existsByBrandIdAndNameIgnoreCase(brand.getId(), req.name())) {
            throw new DataIntegrityViolationException("Organization name already exists under this brand");
        }

        Organization o = new Organization();
        o.setBrand(brand);
        o.setName(req.name());
        o.setDescription(req.description());
        o.setActive(req.active() == null || req.active());
        o.setCode(generateUniqueCode(brand.getId()));

        Organization saved = organizationRepository.save(o);
        return OrganizationMapper.toResponse(saved);
    }

    public OrganizationResponse get(UUID id) {
        Organization o = organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organization not found: " + id));
        return OrganizationMapper.toResponse(o);
    }

    public List<OrganizationResponse> list(UUID brandId) {
        if (brandId != null) {
            return organizationRepository.findAllByBrandId(brandId, Sort.by(Sort.Direction.ASC, "name"))
                    .stream().map(OrganizationMapper::toResponse).toList();
        }
        return organizationRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(OrganizationMapper::toResponse).toList();
    }

    public OrganizationResponse update(UUID id, OrganizationUpdateRequest req) {
        Organization o = organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organization not found: " + id));

        if (!o.getName().equalsIgnoreCase(req.name())
                && organizationRepository.existsByBrandIdAndNameIgnoreCase(o.getBrand().getId(), req.name())) {
            throw new DataIntegrityViolationException("Organization name already exists under this brand");
        }

        OrganizationMapper.applyUpdate(o, req);
        return OrganizationMapper.toResponse(o);
    }

    public void delete(UUID id) {
        Organization o = organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organization not found: " + id));
        organizationRepository.delete(o);
    }

    private String generateUniqueCode(UUID brandId) {
        String code;
        int tries = 0;
        do {
            code = PREFIX + randomToken(6);
            tries++;
            if (tries > 10) {
                code = PREFIX + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            }
        } while (organizationRepository.existsByBrandIdAndCodeIgnoreCase(brandId, code));
        return code;
    }

    private String randomToken(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPHANUM.charAt(rnd.nextInt(ALPHANUM.length())));
        return sb.toString();
    }
}
