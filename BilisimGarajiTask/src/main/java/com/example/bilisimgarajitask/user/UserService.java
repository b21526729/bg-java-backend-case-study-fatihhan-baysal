package com.example.bilisimgarajitask.user;

import com.example.bilisimgarajitask.classroom.Classroom;
import com.example.bilisimgarajitask.classroom.ClassroomRepository;
import com.example.bilisimgarajitask.common.NotFoundException;
import com.example.bilisimgarajitask.organization.Organization;
import com.example.bilisimgarajitask.organization.OrganizationRepository;
import com.example.bilisimgarajitask.teacher.TeacherClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static com.example.bilisimgarajitask.user.Role.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository repoUser;
    private final OrganizationRepository repoOrg;
    private final ClassroomRepository repoClass;
    private final TeacherClassroomRepository repoTeacherClass;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserResponse create(UserCreateRequest req){
        if (repoUser.existsByEmailIgnoreCase(req.email())) {
            throw new DataIntegrityViolationException("Email already in use");
        }

        User u = new User();
        u.setEmail(req.email());
        u.setPassword(encoder.encode(req.password()));
        u.setFirstName(req.firstName());
        u.setLastName(req.lastName());



        u.setProfileId(req.profileId());

        u.setRole(switch ((req.profileId())){
            case 0     -> SUPER_ADMIN;
            case 1     -> TEACHER;
            case 2     -> STUDENT;
            default -> throw new ResponseStatusException(
                    BAD_REQUEST, "Invalid profile_id: must be 0,1,2");
        });
        u.setActive(req.active() == null || req.active());

        if (u.getRole().equals(STUDENT)) {
            if (req.organizationId() == null || req.classroomId() == null) {
                throw new ResponseStatusException(
                        BAD_REQUEST,
                        "Student must have organizationId and classroomId"
                );
            }
            Organization org = repoOrg.findById(req.organizationId())
                    .orElseThrow(() -> new NotFoundException("Organization not found: " + req.organizationId()));
            Classroom cls = repoClass.findById(req.classroomId())
                    .orElseThrow(() -> new NotFoundException("Classroom not found: " + req.classroomId()));
            if (!cls.getOrganization().getId().equals(org.getId())) {
                throw new ResponseStatusException(BAD_REQUEST, "Classroom does not belong to the given organization");
            }
            u.setOrganization(org);
            u.setClassroom(cls);
        } else {
            u.setOrganization(null);
            u.setClassroom(null);
        }

        return UserMapper.toResponse(repoUser.save(u));
    }

    public UserResponse get(UUID id){
        User u = repoUser.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        return UserMapper.toResponse(u);
    }

    public List<UserResponse> list(Role role){
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (role != null){
            return repoUser.findAllByRole(role, sort).stream()
                    .map(UserMapper::toResponse).toList();
        }
        return repoUser.findAll(sort).stream()
                .map(UserMapper::toResponse).toList();
    }

    public UserResponse update(UUID id, UserUpdateRequest req){
        User u = repoUser.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        UserMapper.applyUpdate(u, req);
        if (req.password() != null && !req.password().isBlank()){
            u.setPassword(encoder.encode(req.password()));
        }
        return UserMapper.toResponse(u);
    }

    public void delete(UUID id){
        User u = repoUser.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        if (u.getRole() == TEACHER) {
            long assigns = repoTeacherClass.countByTeacherId(u.getId());
            if (assigns > 0) throw new ResponseStatusException(BAD_REQUEST,"Teacher has classroom assignments");
        }
        // STUDENT silerken özel kural istiyorsan burada kontrol edebilirsin (örn. notlar vs.)
        repoUser.delete(u);
    }

}
