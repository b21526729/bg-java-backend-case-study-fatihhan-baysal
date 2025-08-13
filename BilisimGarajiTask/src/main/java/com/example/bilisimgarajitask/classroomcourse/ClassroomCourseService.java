package com.example.bilisimgarajitask.classroomcourse;

import com.example.bilisimgarajitask.classroom.Classroom;
import com.example.bilisimgarajitask.classroom.ClassroomRepository;
import com.example.bilisimgarajitask.common.NotFoundException;
import com.example.bilisimgarajitask.course.Course;
import com.example.bilisimgarajitask.course.CourseRepository;
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
public class ClassroomCourseService {

    private final ClassroomCourseRepository repo;
    private final ClassroomRepository classroomRepo;
    private final CourseRepository courseRepo;

    public ClassroomCourseResponse assign(ClassroomCourseAssignRequest req){
        Classroom classroom = classroomRepo.findById(req.classroomId())
                .orElseThrow(() -> new NotFoundException("Classroom not found: " + req.classroomId()));
        Course course = courseRepo.findById(req.courseId())
                .orElseThrow(() -> new NotFoundException("Course not found: " + req.courseId()));

        if (repo.existsByClassroomIdAndCourseId(classroom.getId(), course.getId())) {
            throw new DataIntegrityViolationException("Course already assigned to this classroom");
        }

        ClassroomCourse cc = new ClassroomCourse();
        cc.setClassroom(classroom);
        cc.setCourse(course);
        cc.setActive(req.active() == null || req.active());

        return ClassroomCourseMapper.toResponse(repo.save(cc));
    }

    @Transactional(readOnly = true)
    public List<ClassroomCourseResponse> list(UUID classroomId, UUID courseId){
        Sort sort = Sort.by(Sort.Direction.ASC, "classroom.name")
                .and(Sort.by(Sort.Direction.ASC, "course.name"));
        if (classroomId != null){
            return repo.findAllByClassroomId(classroomId, sort).stream()
                    .map(ClassroomCourseMapper::toResponse).toList();
        }
        if (courseId != null){
            return repo.findAllByCourseId(courseId, sort).stream()
                    .map(ClassroomCourseMapper::toResponse).toList();
        }
        return repo.findAll(sort).stream().map(ClassroomCourseMapper::toResponse).toList();
    }

    public void unassign(UUID classroomId, UUID courseId){
        ClassroomCourse cc = repo.findByClassroomIdAndCourseId(classroomId, courseId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
        repo.delete(cc);
    }
}
