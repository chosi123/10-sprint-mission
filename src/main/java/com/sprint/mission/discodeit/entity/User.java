package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "users")
public class User extends BaseUpdatableEntity {
    @NotNull
    @Size(min = 1, max = 50)
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotNull
    @Size(min = 1, max = 60)
    @Column(nullable = false)
    private String password;

    @OneToOne
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile;

    @OneToOne(mappedBy = "user")
    private UserStatus userStatus;

    public User(String username, String email, String password, BinaryContent profile) {
        super();
        //
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public void isUpdated(){
        this.updatedAt = Instant.now();
    }
}
