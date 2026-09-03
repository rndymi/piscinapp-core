package com.rndymi.es.piscinapp.core.maintenance.domain;

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
        name = "maintenance_activities"
)
public class MaintenanceActivity {

    public static final int NAME_MAX_LENGTH = 150;
    public static final int DESCRIPTION_MAX_LENGTH = 1000;

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

    @Column(
            length = DESCRIPTION_MAX_LENGTH
    )
    private String description;

    @Column(nullable = false)
    private boolean active;

    public MaintenanceActivity(
            UUID id,
            String name,
            String description
    ) {

        this.id =
                id;

        this.name =
                name;

        this.description =
                description;

        this.active =
                true;
    }

    public void update(
            String name,
            String description
    ) {

        this.name =
                name;

        this.description =
                description;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
