package com.rndymi.es.piscinapp.core.pools.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(
        name = "swimming_pools"
)
public class SwimmingPool {

    public static final int
            NAME_MAX_LENGTH =
            150;

    public static final int
            ADDRESS_MAX_LENGTH =
            300;

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
            nullable = false,
            length = ADDRESS_MAX_LENGTH
    )
    private String address;

    @Column(nullable = false)
    private boolean active;

    protected SwimmingPool() {
    }

    public SwimmingPool(
            UUID id,
            String name,
            String address
    ) {

        this.id =
                id;

        this.name =
                name;

        this.address =
                address;

        this.active =
                true;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public boolean isActive() {
        return active;
    }

    public void update(
            String name,
            String address
    ) {

        this.name =
                name;

        this.address =
                address;
    }

    public void activate() {

        this.active =
                true;
    }

    public void deactivate() {

        this.active =
                false;
    }
}
