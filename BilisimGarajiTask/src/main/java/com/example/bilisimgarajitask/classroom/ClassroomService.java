package com.example.bilisimgarajitask.classroom;

import com.example.bilisimgarajitask.classroomcourse.ClassroomCourseRepository;
import com.example.bilisimgarajitask.common.NotFoundException;
import com.example.bilisimgarajitask.organization.Organization;
import com.example.bilisimgarajitask.organization.OrganizationRepository;
import com.example.bilisimgarajitask.teacher.TeacherClassroomRepository;
import com.example.bilisimgarajitask.user.Role;
import com.example.bilisimgarajitask.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassroomService {

    private final ClassroomRepository repoClass;
    private final OrganizationRepository repoOrg;

    private final ClassroomCourseRepository repoClassCourse;

    private final TeacherClassroomRepository repoTeacherClass;

    private final UserRepository repoUser;

    public ClassroomResponse create(ClassroomCreateRequest req) {
        Organization org = repoOrg.findById(req.organizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found: " + req.organizationId()));

        if (repoClass.existsByOrganizationIdAndNameIgnoreCase(org.getId(), req.name())) {
            throw new DataIntegrityViolationException("Classroom name already exists under this organization");
        }

        Classroom c = new Classroom();
        c.setOrganization(org);
        c.setName(req.name());
        c.setGrade(req.grade());
        c.setSection(req.section());
        c.setActive(req.active() == null || req.active());

        Classroom saved = repoClass.save(c);
        return ClassroomMapper.toResponse(saved);
    }

    public ClassroomResponse get(UUID id) {
        Classroom c = repoClass.findById(id)
                .orElseThrow(() -> new NotFoundException("Classroom not found: " + id));
        return ClassroomMapper.toResponse(c);
    }

    public List<ClassroomResponse> list(UUID organizationId) {
        if (organizationId != null) {
            return repoClass
                    .findAllByOrganizationId(organizationId, Sort.by(Sort.Direction.ASC, "name"))
                    .stream().map(ClassroomMapper::toResponse).toList();
        }
        return repoClass.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(ClassroomMapper::toResponse).toList();
    }

    public ClassroomResponse update(UUID id, ClassroomUpdateRequest req) {
        Classroom c = repoClass.findById(id)
                .orElseThrow(() -> new NotFoundException("Classroom not found: " + id));

        if (!c.getName().equalsIgnoreCase(req.name())
                && repoClass.existsByOrganizationIdAndNameIgnoreCase(c.getOrganization().getId(), req.name())) {
            throw new DataIntegrityViolationException("Classroom name already exists under this organization");
        }

        ClassroomMapper.applyUpdate(c, req);
        return ClassroomMapper.toResponse(c);
    }

    public void delete(UUID id) {
        Classroom c = repoClass.findById(id)
                .orElseThrow(() -> new NotFoundException("Classroom not found: " + id));

        long tc = repoTeacherClass.countByClassroomId(c.getId());
        if (tc > 0) throw new ResponseStatusException(BAD_REQUEST,"Classroom has teacher assignments");

        long cc = repoClassCourse.countByClassroomId(c.getId());
        if (cc > 0) throw new ResponseStatusException(BAD_REQUEST,"Classroom has course assignments");

        long students = repoUser.countByClassroomIdAndRole(c.getId(), Role.STUDENT);
        if (students > 0) throw new ResponseStatusException(BAD_REQUEST,"Classroom has students");

        repoClass.delete(c);
    }

}
