package com.sprint.mission.discodeit.exception;

public class PrivateChannelUpdateException extends RuntimeException {
    public PrivateChannelUpdateException() {
        super("Private channel cannot be updated");
    }
}
