package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;

@Entity(name = "channels")
@NoArgsConstructor
@Getter
public class Channel extends BaseUpdatableEntity {
    //
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ChannelType type;

    @Size(max = 100)
    @Column(length = 100)
    private String name = null;

    @Size(max = 500)
    @Column(length = 500)
    private String description = null;

    public Channel(ChannelType type, String name, String description) {
        super();
        //
        this.type = type;

        if(type == PRIVATE){
            //participantIds = new ArrayList<>();
        }
        else{
            this.name = name;
            this.description = description;
        }

    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if(type==PRIVATE) throw new IllegalStateException("Private Channel cannot be updated");

        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
/*
    public void join(UUID userId){
        participantIds.add(userId);
    }
    */
}
