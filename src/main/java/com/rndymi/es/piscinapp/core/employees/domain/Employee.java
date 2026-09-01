package com.rndymi.es.piscinapp.core.employees.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
        name = "employees",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_employees_user_account_id",
                columnNames = "user_account_id"
        )
)
public class Employee {

    public static final int FIRST_NAME_MAX_LENGTH = 100;

    public static final int FAMILY_NAME_MAX_LENGTH = 150;

    @Id
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID id;

    @Column(
            name = "first_name",
            nullable = false,
            length = FIRST_NAME_MAX_LENGTH
    )
    private String firstName;

    @Column(
            name = "family_name",
            nullable = false,
            length = FAMILY_NAME_MAX_LENGTH
    )
    private String familyName;

    @Column(nullable = false)
    private boolean active;

    @Column(
            name = "user_account_id",
            unique = true
    )
    private UUID userAccountId;

    protected Employee() {
    }

    public Employee(
            UUID id,
            String firstName,
            String familyName
    ) {

        this.id = id;
        this.firstName = firstName;
        this.familyName = familyName;
        this.active = true;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public boolean isActive() {
        return active;
    }

    public UUID getUserAccountId() {
        return userAccountId;
    }

    public String getDisplayName() {

        return firstName
                + " "
                + familyName;
    }

    public void updateName(
            String firstName,
            String familyName
    ) {

        this.firstName = firstName;
        this.familyName = familyName;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void associateAccount(
            UUID userAccountId
    ) {

        this.userAccountId =
                userAccountId;
    }

    public void removeAccountAssociation() {

        this.userAccountId =
                null;
    }
}
