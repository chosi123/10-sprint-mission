package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "message_attachments")
@NoArgsConstructor
@Getter
@Setter
public class MessageAttachment {
    //엔티티 구조도에는 없으나, 키값은 필수이므로 임의로 작성함.
    @EmbeddedId
    private MessageAttachmentId id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "message_id")
    private Message message;

    @OneToOne
    @NotNull
    @JoinColumn(name = "attachment_id", unique = true)
    private BinaryContent binaryContent;

}
