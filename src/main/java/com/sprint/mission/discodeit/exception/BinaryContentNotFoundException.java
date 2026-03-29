package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class BinaryContentNotFoundException extends BinaryContentException {

    public BinaryContentNotFoundException(String message) {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, message);
    }

    public BinaryContentNotFoundException(String message, Map<String, Object> details) {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, message, details);
    }
}
