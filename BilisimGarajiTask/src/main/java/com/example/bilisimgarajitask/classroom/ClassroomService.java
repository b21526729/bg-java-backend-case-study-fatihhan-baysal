package com.example.bilisimgarajitask.classroom;

import com.example.bilisimgarajitask.common.NotFoundException;
import com.example.bilisimgarajitask.organization.Organization;
import com.example.bilisimgarajitask.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final OrganizationRepository organizationRepository;

    public ClassroomResponse create(ClassroomCreateRequest req) {
        Organization org = organizationRepository.findById(req.organizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found: " + req.organizationId()));

        if (classroomRepository.existsByOrganizationIdAndNameIgnoreCase(org.getId(), req.name())) {
            throw new DataIntegrityViolationException("Classroom name already exists under this organization");
        }

        Classroom c = new Classroom();
        c.setOrganization(org);
        c.setName(req.name());
        c.setGrade(req.grade());
        c.setSection(req.section());
        c.setActive(req.active() == null || req.active());

        Classroom saved = classroomRepository.save(c);
        return ClassroomMapper.toResponse(saved);
    }

    public ClassroomResponse get(UUID id) {
        Classroom c = classroomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Classroom not found: " + id));
        return ClassroomMapper.toResponse(c);
    }

    public List<ClassroomResponse> list(UUID organizationId) {
        if (organizationId != null) {
            return classroomRepository
                    .findAllByOrganizationId(organizationId, Sort.by(Sort.Direction.ASC, "name"))
                    .stream().map(ClassroomMapper::toResponse).toList();
        }
        return classroomRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(ClassroomMapper::toResponse).toList();
    }

    public ClassroomResponse update(UUID id, ClassroomUpdateRequest req) {
        Classroom c = classroomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Classroom not found: " + id));

        if (!c.getName().equalsIgnoreCase(req.name())
                && classroomRepository.existsByOrganizationIdAndNameIgnoreCase(c.getOrganization().getId(), req.name())) {
            throw new DataIntegrityViolationException("Classroom name already exists under this organization");
        }

        ClassroomMapper.applyUpdate(c, req);
        return ClassroomMapper.toResponse(c);
    }

    public void delete(UUID id) {
        Classroom c = classroomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Classroom not found: " + id));
        classroomRepository.delete(c);
    }
}
