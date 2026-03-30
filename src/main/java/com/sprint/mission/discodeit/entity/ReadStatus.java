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
@Table(name = "read_statuses",
        uniqueConstraints = {
            @UniqueConstraint(
                name = "uk_read_status_user_channel",
                columnNames = {"user_id", "channel_id"}
            )
})
@Getter
@Setter
public class ReadStatus extends BaseUpdatableEntity {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @NotNull
    @Column(nullable = false)
    private Instant lastReadAt;

    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
    }
}
