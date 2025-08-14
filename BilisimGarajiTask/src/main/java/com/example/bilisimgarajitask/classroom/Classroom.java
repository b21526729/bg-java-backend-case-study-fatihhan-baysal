package com.example.bilisimgarajitask.classroom;

import com.example.bilisimgarajitask.common.BaseEntity;
import com.example.bilisimgarajitask.organization.Organization;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "classrooms",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_class_org_name", columnNames = {"organization_id", "name"})
        },
        indexes = {
                @Index(name = "idx_class_org", columnList = "organization_id")
        })
@Getter @Setter
public class Classroom extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 16)
    private String grade;
    @Column(length = 16)
    private String section;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_class_org"))
    private Organization organization;

    @Column(nullable = false)
    private boolean active = true;
}
