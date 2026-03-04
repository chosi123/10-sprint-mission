package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class MessageAttachments {
    //엔티티 구조도에는 없으나, 키값은 필수이므로 임의로 작성함.
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Message message;

    @OneToOne
    private BinaryContent binaryContent;

}
