package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class BinaryContentException extends DiscodeitException {

    public BinaryContentException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BinaryContentException(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details
    ) {
        super(errorCode, message, details);
    }

    public BinaryContentException(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details,
            Throwable cause
    ) {
        super(errorCode, message, details, cause);
    }
}
