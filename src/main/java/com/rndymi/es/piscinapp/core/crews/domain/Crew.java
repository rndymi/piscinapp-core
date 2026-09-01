package com.rndymi.es.piscinapp.core.crews.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(
        name = "crews"
)
public class Crew {

    public static final int
            NAME_MAX_LENGTH =
            150;

    @Id
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID id;

    @Column(
            nullable = false,
            length = NAME_MAX_LENGTH
    )
    private String name;

    @Column(nullable = false)
    private boolean active;

    @Column(
            name = "supervisor_employee_id"
    )
    private UUID supervisorEmployeeId;

    protected Crew() {
    }

    public Crew(
            UUID id,
            String name
    ) {

        this.id =
                id;

        this.name =
                name;

        this.active =
                true;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public UUID getSupervisorEmployeeId() {
        return supervisorEmployeeId;
    }

    public void rename(
            String name
    ) {

        this.name =
                name;
    }

    public void activate() {

        this.active =
                true;
    }

    public void deactivate() {

        this.active =
                false;
    }

    public void assignSupervisor(
            UUID employeeId
    ) {

        this.supervisorEmployeeId =
                employeeId;
    }

    public void clearSupervisor() {

        this.supervisorEmployeeId =
                null;
    }
}
