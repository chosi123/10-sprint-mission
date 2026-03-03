package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {
    //업데이트 가능.
    protected Instant updatedAt;

    public BaseUpdatableEntity() {
        super();
        this.updatedAt = Instant.now();
    }
}
