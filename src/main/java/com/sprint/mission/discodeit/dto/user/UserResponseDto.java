package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        BinaryContentResponseDto profileImage,
        boolean online
) {
}

