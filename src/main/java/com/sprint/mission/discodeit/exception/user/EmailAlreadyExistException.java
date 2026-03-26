package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class EmailAlreadyExistException extends UserException {
    public EmailAlreadyExistException(String email) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, Map.of("email", email));
    }
}
