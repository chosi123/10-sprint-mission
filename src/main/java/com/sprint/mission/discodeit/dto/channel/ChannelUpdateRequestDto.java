package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record ChannelUpdateRequestDto
        (
                String newName,
                String newDescription
        ) {
}
