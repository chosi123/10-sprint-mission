package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class BaseEntity {
    @Id
    protected final UUID id;

    @CreatedDate
    @NotNull
    @Column(nullable = false, updatable = false)
    protected Instant createdAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
    }
}
