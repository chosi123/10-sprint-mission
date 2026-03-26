package com.sprint.mission.discodeit.exception.binarycontent;

import java.util.Map;

import static com.sprint.mission.discodeit.exception.ErrorCode.PROFILE_SHOULD_IMAGE;

//프로필 사진에 이미지가 아닌 다른 파일이 들어왔을 때
public class WrongImageException extends BinaryContentException {
    public WrongImageException(String fileName, String contentType) {
        super(PROFILE_SHOULD_IMAGE, Map.of("fileName", fileName, "contentType", contentType));
    }
}
