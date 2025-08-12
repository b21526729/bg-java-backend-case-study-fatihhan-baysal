package com.example.bilisimgarajitask.teacher;

import com.example.bilisimgarajitask.common.BaseEntity;
import com.example.bilisimgarajitask.user.User;
import com.example.bilisimgarajitask.classroom.Classroom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "teacher_classrooms",
        uniqueConstraints = @UniqueConstraint(name = "uk_teacher_classroom",
                columnNames = {"teacher_id","classroom_id"}),
        indexes = {
                @Index(name = "idx_tc_teacher", columnList = "teacher_id"),
                @Index(name = "idx_tc_classroom", columnList = "classroom_id")
        })
@Getter @Setter
public class TeacherClassroom extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tc_teacher"))
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classroom_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tc_classroom"))
    private Classroom classroom;

    @Column(nullable = false)
    private boolean active = true;
}
