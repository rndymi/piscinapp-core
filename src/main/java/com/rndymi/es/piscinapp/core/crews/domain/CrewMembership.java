package com.rndymi.es.piscinapp.core.crews.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(
        access = AccessLevel.PROTECTED
)
@Entity
@Table(
        name = "crew_memberships",
        uniqueConstraints =
        @UniqueConstraint(
                name =
                        "uk_crew_memberships_crew_employee",
                columnNames = {
                        "crew_id",
                        "employee_id"
                }
        )
)
public class CrewMembership {

    @Id
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID id;

    @Column(
            name = "crew_id",
            nullable = false
    )
    private UUID crewId;

    @Column(
            name = "employee_id",
            nullable = false
    )
    private UUID employeeId;

    public CrewMembership(
            UUID id,
            UUID crewId,
            UUID employeeId
    ) {

        this.id =
                id;

        this.crewId =
                crewId;

        this.employeeId =
                employeeId;
    }
}
