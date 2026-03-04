package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Entity
@Table(name = "read_statuses")
@Getter
@Setter
public class ReadStatus extends BaseUpdatableEntity {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "channel_id", unique = true)
    private Channel channel;

    @NotNull
    @Column(nullable = false)
    private Instant lastReadAt;

    public ReadStatus(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = Instant.now();
    }
}
