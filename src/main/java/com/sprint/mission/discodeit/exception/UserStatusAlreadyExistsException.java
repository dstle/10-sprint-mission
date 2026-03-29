package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class UserStatusAlreadyExistsException extends UserStatusException {

    public UserStatusAlreadyExistsException(String message) {
        super(ErrorCode.USER_STATUS_ALREADY_EXISTS, message);
    }

    public UserStatusAlreadyExistsException(String message, Map<String, Object> details) {
        super(ErrorCode.USER_STATUS_ALREADY_EXISTS, message, details);
    }
}
