package com.example.bilisimgarajitask.teacher;

import com.example.bilisimgarajitask.classroom.Classroom;
import com.example.bilisimgarajitask.classroom.ClassroomRepository;
import com.example.bilisimgarajitask.common.NotFoundException;
import com.example.bilisimgarajitask.user.Role;
import com.example.bilisimgarajitask.user.User;
import com.example.bilisimgarajitask.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherClassroomService {

    private final TeacherClassroomRepository repo;
    private final UserRepository userRepo;
    private final ClassroomRepository classroomRepo;

    public TeacherClassroomResponse assign(TeacherClassroomAssignRequest req){
        User teacher = userRepo.findById(req.teacherId())
                .orElseThrow(() -> new NotFoundException("Teacher not found: " + req.teacherId()));
        if (teacher.getRole() != Role.TEACHER) {
            throw new AccessDeniedException("User is not a TEACHER");
        }

        Classroom classroom = classroomRepo.findById(req.classroomId())
                .orElseThrow(() -> new NotFoundException("Classroom not found: " + req.classroomId()));

        if (repo.existsByTeacherIdAndClassroomId(teacher.getId(), classroom.getId())) {
            throw new DataIntegrityViolationException("Teacher already assigned to this classroom");
        }

        TeacherClassroom tc = new TeacherClassroom();
        tc.setTeacher(teacher);
        tc.setClassroom(classroom);
        tc.setActive(req.active() == null || req.active());

        return TeacherClassroomMapper.toResponse(repo.save(tc));
    }

    public List<TeacherClassroomResponse> list(UUID teacherId, UUID classroomId){
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (teacherId != null) {
            return repo.findAllByTeacherId(teacherId, sort).stream()
                    .map(TeacherClassroomMapper::toResponse).toList();
        }
        if (classroomId != null) {
            return repo.findAllByClassroomId(classroomId, sort).stream()
                    .map(TeacherClassroomMapper::toResponse).toList();
        }

        return repo.findAll(sort).stream().map(TeacherClassroomMapper::toResponse).toList();
    }

    public void unassign(UUID teacherId, UUID classroomId){
        TeacherClassroom tc = repo.findByTeacherIdAndClassroomId(teacherId, classroomId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
        repo.delete(tc);
    }
}
