package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private final String contentType;
    private final byte[] bytes;
    private final Long size;
    private final String fileName;

    public BinaryContent(byte[] bytes, String contentType, String fileName, Long size){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.bytes = bytes;
        this.contentType = contentType;
        this.fileName = fileName;
        this.size = size;
    }
}
