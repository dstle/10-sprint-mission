package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class ChannelNotFoundException extends ChannelException {

    public ChannelNotFoundException(String message) {
        super(ErrorCode.CHANNEL_NOT_FOUND, message);
    }

    public ChannelNotFoundException(String message, Map<String, Object> details) {
        super(ErrorCode.CHANNEL_NOT_FOUND, message, details);
    }
}
