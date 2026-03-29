package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class MessageNotFoundException extends MessageException {

    public MessageNotFoundException(String message) {
        super(ErrorCode.MESSAGE_NOT_FOUND, message);
    }

    public MessageNotFoundException(String message, Map<String, Object> details) {
        super(ErrorCode.MESSAGE_NOT_FOUND, message, details);
    }
}
