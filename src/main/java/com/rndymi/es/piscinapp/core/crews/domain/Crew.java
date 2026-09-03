package com.rndymi.es.piscinapp.core.crews.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
        name = "crews"
)
public class Crew {

    public static final int NAME_MAX_LENGTH = 150;

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
