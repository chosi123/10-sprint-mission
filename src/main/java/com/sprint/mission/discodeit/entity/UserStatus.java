package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@Entity(name = "user_statuses")
@Getter
@Setter
public class UserStatus extends BaseUpdatableEntity{
    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @NotNull
    @Column(nullable = false)
    private Instant lastActiveAt;

    public UserStatus(User user) {
        this.user = user;
        this.lastActiveAt = Instant.now();//생성 시점을 첫 접속으로 설정
    }

    public Boolean isOnline(){
        if(lastActiveAt == null) return false;

        return !Instant.now().minusSeconds(360).isAfter(lastActiveAt);
    }

}
