package com.example.bilisimgarajitask.user;

import com.example.bilisimgarajitask.classroom.Classroom;
import com.example.bilisimgarajitask.classroom.ClassroomRepository;
import com.example.bilisimgarajitask.common.NotFoundException;
import com.example.bilisimgarajitask.organization.Organization;
import com.example.bilisimgarajitask.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ClassroomRepository classroomRepository;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserResponse create(UserCreateRequest req){
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new DataIntegrityViolationException("Email already in use");
        }

        User u = new User();
        u.setEmail(req.email());
        u.setPassword(encoder.encode(req.password()));
        u.setFirstName(req.firstName());
        u.setLastName(req.lastName());
        u.setRole(req.role());
        u.setProfileId(switch (req.role()){
            case SUPER_ADMIN -> 0;
            case TEACHER     -> 1;
            case STUDENT     -> 2;
        });
        u.setActive(req.active() == null || req.active());

        if (req.role() == Role.STUDENT) {
            if (req.organizationId() == null || req.classroomId() == null) {
                throw new DataIntegrityViolationException("Student must have organizationId and classroomId");
            }
            Organization org = organizationRepository.findById(req.organizationId())
                    .orElseThrow(() -> new NotFoundException("Organization not found: " + req.organizationId()));
            Classroom cls = classroomRepository.findById(req.classroomId())
                    .orElseThrow(() -> new NotFoundException("Classroom not found: " + req.classroomId()));
            if (!cls.getOrganization().getId().equals(org.getId())) {
                throw new DataIntegrityViolationException("Classroom does not belong to the given organization");
            }
            u.setOrganization(org);
            u.setClassroom(cls);
        } else {
            u.setOrganization(null);
            u.setClassroom(null);
        }

        return UserMapper.toResponse(userRepository.save(u));
    }

    public UserResponse get(UUID id){
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        return UserMapper.toResponse(u);
    }

    public List<UserResponse> list(Role role){
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (role != null){
            return userRepository.findAllByRole(role, sort).stream()
                    .map(UserMapper::toResponse).toList();
        }
        return userRepository.findAll(sort).stream()
                .map(UserMapper::toResponse).toList();
    }

    public UserResponse update(UUID id, UserUpdateRequest req){
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        UserMapper.applyUpdate(u, req);
        if (req.password() != null && !req.password().isBlank()){
            u.setPassword(encoder.encode(req.password()));
        }
        return UserMapper.toResponse(u);
    }

    public void delete(UUID id){
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        // TODO: Öğretmen atamaları vs. varsa engelle (TeacherClassroom)
        userRepository.delete(u);
    }
}
