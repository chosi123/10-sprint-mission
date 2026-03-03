package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class BaseEntity {
    @Id
    protected final UUID id;

    @NotNull
    @Column(nullable = false, updatable = false)
    protected final Instant createdAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }
}
