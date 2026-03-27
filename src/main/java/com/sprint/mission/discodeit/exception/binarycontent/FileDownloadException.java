package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class FileDownloadException extends BinaryContentException{
    public FileDownloadException(){
        super(ErrorCode.DOWNLOAD_FAILED);
    }
}
