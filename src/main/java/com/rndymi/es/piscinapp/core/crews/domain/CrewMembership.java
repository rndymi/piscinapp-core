package com.rndymi.es.piscinapp.core.crews.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

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

    protected CrewMembership() {
    }

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

    public UUID getId() {
        return id;
    }

    public UUID getCrewId() {
        return crewId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }
}
