package com.sprint.mission.discodeit.mapper.binarycontent;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BinaryContentProvider {
    private final BinaryContentStorage storage; // 여기서 스토리지 주입

    @Named("fetchBytesFromId")
    public byte[] fetchBytes(UUID id) {
        if (id == null) return null;
        try {
            return storage.get(id).readAllBytes();
        } catch (Exception e) {
            return new byte[0]; // 혹은 예외 처리
        }
    }
}