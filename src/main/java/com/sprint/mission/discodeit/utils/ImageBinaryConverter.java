package com.sprint.mission.discodeit.utils;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public class ImageBinaryConverter {

    public static byte[] convert(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new DiscodeitException(
                    ErrorCode.IMAGE_BINARY_CONVERSION_FAILED,
                    "이미지 파일 변환을 실패하였습니다. multipartFile: " + multipartFile
            );
        }
    }
}
