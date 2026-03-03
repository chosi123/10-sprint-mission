package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record LoginResponseDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String password,
        String email,
        UUID profileId
) {
}
