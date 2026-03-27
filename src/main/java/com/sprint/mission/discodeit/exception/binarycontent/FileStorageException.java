package com.sprint.mission.discodeit.exception.binarycontent;

import static com.sprint.mission.discodeit.exception.ErrorCode.FILE_STORAGE_FAILED;

public class FileStorageException extends BinaryContentException {
    public FileStorageException() {
        super(FILE_STORAGE_FAILED);
    }
}
