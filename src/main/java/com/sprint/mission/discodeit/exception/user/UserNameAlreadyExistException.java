package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserNameAlreadyExistException extends UserException {
    public UserNameAlreadyExistException(String username) {
        super(ErrorCode.USERNAME_ALREADY_EXISTS, Map.of("username", username));
    }
}
