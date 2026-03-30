package com.sprint.mission.discodeit.exception.binarycontent;

import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.exception.ErrorCode.FILE_NOT_FOUND;

public class BinaryContentNotFoundException extends BinaryContentException {
    public BinaryContentNotFoundException(UUID id) {
        super(FILE_NOT_FOUND, Map.of("id", id));
    }
}
