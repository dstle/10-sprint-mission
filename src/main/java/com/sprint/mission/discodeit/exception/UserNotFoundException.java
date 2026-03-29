package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(String message) {
        super(ErrorCode.USER_NOT_FOUND, message);
    }

    public UserNotFoundException(String message, Map<String, Object> details) {
        super(ErrorCode.USER_NOT_FOUND, message, details);
    }
}
