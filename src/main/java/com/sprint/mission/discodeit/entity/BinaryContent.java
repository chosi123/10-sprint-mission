package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Entity(name = "binary_contents")
public class BinaryContent extends BaseEntity{
    @NotNull
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String contentType;

    @NotNull
    @Column(nullable = false)
    private byte[] bytes;

    @NotNull
    @Column(nullable = false)
    private Long size;

    @NotNull
    @Column(nullable = false)
    private String fileName;

    public BinaryContent(byte[] bytes, String contentType, String fileName, Long size){
        super();
        this.bytes = bytes;
        this.contentType = contentType;
        this.fileName = fileName;
        this.size = size;
    }
}
