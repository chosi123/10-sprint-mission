package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

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

    public User(String username, String email, String password, UUID profileId) {
        super();
        //
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void isUpdated(){
        this.updatedAt = Instant.now();
    }
}
