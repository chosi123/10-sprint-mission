package com.sprint.mission.discodeit.exception.readstatus;

import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.exception.ErrorCode.READ_STATUS_NOT_FOUND;

public class ReadStatusNotFoundException extends ReadStatusException {
    public ReadStatusNotFoundException(UUID readStatusId) {
        super(READ_STATUS_NOT_FOUND, Map.of("readStatusId", readStatusId));
    }

    public ReadStatusNotFoundException() {
        super(READ_STATUS_NOT_FOUND);
    }
}
