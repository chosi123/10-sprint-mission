package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class ReadStatusNotFoundException extends RuntimeException {
    public ReadStatusNotFoundException(UUID readStatusId) {
        super("readStatus with " + readStatusId + " not found");
    }

    public ReadStatusNotFoundException() {
        super("readStatus not found");
    }
}
