package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class ChannelNotFoundException extends RuntimeException {
    public ChannelNotFoundException(UUID messageId) {
        super("Channel with id " + messageId + " not found");
    }
}
