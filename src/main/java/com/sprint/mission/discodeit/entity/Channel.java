package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;

@Getter
public class Channel extends BaseUpdatableEntity {
    //
    private ChannelType type;
    private String name = null;
    private String description = null;
    private List<UUID> participantIds = null;

    public Channel(ChannelType type, String name, String description) {
        super();
        //
        this.type = type;

        if(type == PRIVATE){
            participantIds = new ArrayList<>();
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

    public void join(UUID userId){
        participantIds.add(userId);
    }

    public void leave(UUID userId){
        if(!participantIds.contains(userId)) participantIds.remove(userId);
    }
}
