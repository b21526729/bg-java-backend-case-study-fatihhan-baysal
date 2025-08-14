package com.example.bilisimgarajitask.classroomcourse;

import com.example.bilisimgarajitask.common.BaseEntity;
import com.example.bilisimgarajitask.classroom.Classroom;
import com.example.bilisimgarajitask.course.Course;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "classroom_courses",
        uniqueConstraints = @UniqueConstraint(name = "uk_classroom_course",
                columnNames = {"classroom_id","course_id"}),
        indexes = {
                @Index(name = "idx_cc_classroom", columnList = "classroom_id"),
                @Index(name = "idx_cc_course", columnList = "course_id")
        })
@Getter @Setter
public class ClassroomCourse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classroom_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cc_classroom"))
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cc_course"))
    private Course course;

    @Column(nullable = false)
    private boolean active = true;
}
