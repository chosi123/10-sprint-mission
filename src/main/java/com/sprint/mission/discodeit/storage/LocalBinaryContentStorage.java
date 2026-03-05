package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {
    @Value("${discodeit.storage.local.root-path}")
    private Path root;

    @PostConstruct
    private void init(){

    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        return null;
    }

    @Override
    public InputStream get(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<?> download(BinaryContentResponseDto binaryContentResponseDto) {
        return null;
    }
}
