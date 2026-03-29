package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class MessageSenderMismatchException extends MessageException {

    public MessageSenderMismatchException(String message) {
        super(ErrorCode.MESSAGE_SENDER_MISMATCH, message);
    }

    public MessageSenderMismatchException(String message, Map<String, Object> details) {
        super(ErrorCode.MESSAGE_SENDER_MISMATCH, message, details);
    }
}
