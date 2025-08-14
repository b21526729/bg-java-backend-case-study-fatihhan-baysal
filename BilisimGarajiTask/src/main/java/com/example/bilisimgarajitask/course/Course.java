package com.example.bilisimgarajitask.course;

import com.example.bilisimgarajitask.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "courses",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_code", columnNames = "code"))
@Getter @Setter
public class Course extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 32)
    private String code;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
