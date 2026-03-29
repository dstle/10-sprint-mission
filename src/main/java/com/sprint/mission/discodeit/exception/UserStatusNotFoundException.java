package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class UserStatusNotFoundException extends UserStatusException {

    public UserStatusNotFoundException(String message) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, message);
    }

    public UserStatusNotFoundException(String message, Map<String, Object> details) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, message, details);
    }
}
