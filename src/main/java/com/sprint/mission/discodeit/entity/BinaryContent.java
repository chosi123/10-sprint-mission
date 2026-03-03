package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent extends BaseEntity{
    private final String contentType;
    private final byte[] bytes;
    private final Long size;
    private final String fileName;

    public BinaryContent(byte[] bytes, String contentType, String fileName, Long size){
        super();
        this.bytes = bytes;
        this.contentType = contentType;
        this.fileName = fileName;
        this.size = size;
    }
}
