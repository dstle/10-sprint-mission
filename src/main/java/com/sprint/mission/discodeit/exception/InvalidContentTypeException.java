package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class InvalidContentTypeException extends BinaryContentException {

    public InvalidContentTypeException(String message) {
        super(ErrorCode.INVALID_CONTENT_TYPE, message);
    }

    public InvalidContentTypeException(String message, Map<String, Object> details) {
        super(ErrorCode.INVALID_CONTENT_TYPE, message, details);
    }
}
