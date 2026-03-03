package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Entity(name = "messages")
@Getter
public class Message extends BaseUpdatableEntity{
    @Column
    private String content;
    //
    @NotNull
    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    private List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachments) {
        super();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachments;
    }

    public void update(String newContent, List<UUID> newAttachments) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }
        if (newAttachments != null && !newAttachments.equals(this.attachmentIds)) {
            this.attachmentIds = newAttachments;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
