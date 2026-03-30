package com.sprint.mission.discodeit.exception.channel;

import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.exception.ErrorCode.CHANNEL_NOT_PUBLIC;

public class PrivateChannelUpdateException extends ChannelException {
    public PrivateChannelUpdateException(UUID channelId) {
        super(CHANNEL_NOT_PUBLIC, Map.of("channelId", channelId));
    }
}
