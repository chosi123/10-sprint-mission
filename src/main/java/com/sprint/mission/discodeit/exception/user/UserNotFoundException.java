package com.sprint.mission.discodeit.exception.user;

import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.exception.ErrorCode.USER_NOT_FOUND;

public class UserNotFoundException extends UserException{

    public UserNotFoundException(String username) {
        super(USER_NOT_FOUND, Map.of("username", username));
    }

    public UserNotFoundException(UUID userId) {
        super(USER_NOT_FOUND, Map.of("userId", userId));
    }
}
