package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(UUID messageId) {
        super("Message with id " + messageId + " not found");
    }
}
