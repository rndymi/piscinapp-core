package com.rndymi.es.piscinapp.core.pools.domain;

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
        name = "swimming_pools"
)
public class SwimmingPool {

    public static final int NAME_MAX_LENGTH = 150;
    public static final int ADDRESS_MAX_LENGTH = 300;

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
