package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record IsMessageReadRequestDto(
        UUID userId,
        UUID messageId
) {
}
