package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {
    //업데이트 가능.
    @LastModifiedDate
    protected Instant updatedAt;

    public BaseUpdatableEntity() {
        super();
        this.updatedAt = Instant.now();
    }
}
