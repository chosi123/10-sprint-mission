package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    protected UUID id;

    @CreatedDate
    @NotNull
    @Column(nullable = false, updatable = false)
    protected Instant createdAt;
}
