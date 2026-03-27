package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileStorageException;
import com.sprint.mission.discodeit.exception.binarycontent.WrongImageException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.WrongPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<?> WrongPasswordHandler(WrongPasswordException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("status", HttpStatus.UNAUTHORIZED.value(),
                        "message", e.getMessage())
        );
    }

    @ExceptionHandler(PrivateChannelUpdateException.class)
    public ResponseEntity<ErrorResponse> notAcceptableHandler(DiscodeitException e){
        log.warn("{}: {}", e.getMessage(), e.getDetails());
        return toErrorResponse(e, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler({UserNameAlreadyExistException.class,
            EmailAlreadyExistException.class,
            WrongImageException.class})
    public ResponseEntity<ErrorResponse> badRequestExceptionHandler(DiscodeitException e) {
        log.warn("{}: {}", e.getMessage(), e.getDetails());
        return toErrorResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            ChannelNotFoundException.class,
            MessageNotFoundException.class,
            BinaryContentNotFoundException.class,
            ReadStatusNotFoundException.class})
    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(DiscodeitException e) {
        log.warn("{}: {}", e.getMessage(), e.getDetails());
        return toErrorResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> internalServerErrorExceptionHandler(DiscodeitException e) {
        log.error("{}: {}", e.getMessage(), e.getDetails());
        return toErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> toErrorResponse(DiscodeitException e, HttpStatus status) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        e.getTimestamp(),
                        e.getErrorCode().name(),
                        e.getErrorCode().getMessage(),
                        e.getDetails(),
                        e.getClass().getName(),
                        status.value()
                )
        );
    }
}