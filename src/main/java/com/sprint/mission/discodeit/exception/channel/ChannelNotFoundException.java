package com.sprint.mission.discodeit.exception.channel;

import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.exception.ErrorCode.CHANNEL_NOT_FOUND;

public class ChannelNotFoundException extends ChannelException {
    public ChannelNotFoundException(UUID messageId) {
        super(CHANNEL_NOT_FOUND, Map.of("messageId", messageId));
    }
}
