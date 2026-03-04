package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

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
