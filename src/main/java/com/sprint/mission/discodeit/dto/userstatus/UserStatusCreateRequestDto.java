package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequestDto(
        @NotBlank
        UUID userId
) {
}
