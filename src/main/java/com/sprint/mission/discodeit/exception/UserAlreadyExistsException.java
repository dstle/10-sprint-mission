package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class UserAlreadyExistsException extends UserException {

    public UserAlreadyExistsException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public UserAlreadyExistsException(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details
    ) {
        super(errorCode, message, details);
    }
}
