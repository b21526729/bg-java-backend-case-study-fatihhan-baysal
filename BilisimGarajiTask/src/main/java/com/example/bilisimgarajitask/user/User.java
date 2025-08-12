package com.example.bilisimgarajitask.user;

import com.example.bilisimgarajitask.common.BaseEntity;
import com.example.bilisimgarajitask.organization.Organization;
import com.example.bilisimgarajitask.classroom.Classroom;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
        indexes = {
                @Index(name = "idx_user_role", columnList = "role"),
                @Index(name = "idx_user_org", columnList = "organization_id"),
                @Index(name = "idx_user_classroom", columnList = "classroom_id")
        })
@Getter @Setter
public class User extends BaseEntity {

    @Email @NotBlank
    @Column(nullable = false, length = 180)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String firstName;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "profile_id", nullable = false)
    private Integer profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id",
            foreignKey = @ForeignKey(name = "fk_user_org"))
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id",
            foreignKey = @ForeignKey(name = "fk_user_classroom"))
    private Classroom classroom;

    @Column(nullable = false)
    private boolean active = true;
}
