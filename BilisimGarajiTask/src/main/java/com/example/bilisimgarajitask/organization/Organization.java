package com.example.bilisimgarajitask.organization;

import com.example.bilisimgarajitask.common.BaseEntity;
import com.example.bilisimgarajitask.brand.Brand;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "organizations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_org_brand_name", columnNames = {"brand_id", "name"}),
                @UniqueConstraint(name = "uk_org_brand_code", columnNames = {"brand_id", "code"})
        },
        indexes = {
                @Index(name = "idx_org_brand", columnList = "brand_id")
        })
@Getter @Setter
public class Organization extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 32)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_org_brand"))
    private Brand brand;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
