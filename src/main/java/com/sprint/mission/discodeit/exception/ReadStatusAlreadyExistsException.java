package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

    public ReadStatusAlreadyExistsException(String message) {
        super(ErrorCode.READ_STATUS_ALREADY_EXISTS, message);
    }

    public ReadStatusAlreadyExistsException(String message, Map<String, Object> details) {
        super(ErrorCode.READ_STATUS_ALREADY_EXISTS, message, details);
    }
}
