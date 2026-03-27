package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다."),
    WRONG_PASSWORD("잘못된 비밀번호입니다."),
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    CHANNEL_NOT_PUBLIC("공개된 채널의 정보만을 수정할 수 있습니다."),
    USERNAME_ALREADY_EXISTS("이미 존재하는 사용자 이름입니다."),
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다."),
    PROFILE_SHOULD_IMAGE("이미지 파일이 아닌 파일은 프로필 이미지로 사용할 수 없습니다."),
    MESSAGE_NOT_FOUND("해당 메시지를 찾을 수 없습니다."),
    FILE_STORAGE_FAILED("파일 저장 중 오류가 발생했습니다."),
    FILE_NOT_FOUND("파일을 찾을 수 없습니다."),
    READ_STATUS_NOT_FOUND("연결된 Readstatus 객체를 찾을 수 없습니다."),
    DOWNLOAD_FAILED("파일 다운로드에 실패했습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
