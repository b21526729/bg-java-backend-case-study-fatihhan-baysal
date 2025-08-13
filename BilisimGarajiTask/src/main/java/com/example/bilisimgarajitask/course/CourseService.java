package com.example.bilisimgarajitask.course;

import com.example.bilisimgarajitask.classroomcourse.ClassroomCourseRepository;
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
public class CourseService {

    private final CourseRepository repoCourse;
    private final ClassroomCourseRepository repoClassCourse;

    private static final String PREFIX = "CRS-";
    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Random rnd = new Random();

    public CourseResponse create(CourseCreateRequest req) {
        Course c = new Course();
        c.setName(req.name());
        c.setDescription(req.description());
        c.setActive(req.active() == null || req.active());
        c.setCode(generateUniqueCode());
        Course saved = repoCourse.save(c);
        return CourseMapper.toResponse(saved);
    }

    public CourseResponse get(UUID id) {
        Course c = repoCourse.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));
        return CourseMapper.toResponse(c);
    }

    public List<CourseResponse> list(Boolean active) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (active != null) {
            return repoCourse.findAllByActive(active, sort)
                    .stream().map(CourseMapper::toResponse).toList();
        }
        return repoCourse.findAll(sort).stream().map(CourseMapper::toResponse).toList();
    }

    public CourseResponse update(UUID id, CourseUpdateRequest req) {
        Course c = repoCourse.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));
        CourseMapper.applyUpdate(c, req);
        return CourseMapper.toResponse(c);
    }

    public void delete(UUID id) {
        Course c = repoCourse.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));

        long used = repoClassCourse.countByCourseId(c.getId());
        if (used > 0) throw new DataIntegrityViolationException("Course is assigned to classrooms");

        repoCourse.delete(c);
    }


    private String generateUniqueCode() {
        String code;
        int tries = 0;
        do {
            code = PREFIX + randomToken(6);
            tries++;
            if (tries > 10) {
                code = PREFIX + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            }
        } while (repoCourse.existsByCodeIgnoreCase(code));
        return code;
    }

    private String randomToken(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPHANUM.charAt(rnd.nextInt(ALPHANUM.length())));
        return sb.toString();
    }
}
