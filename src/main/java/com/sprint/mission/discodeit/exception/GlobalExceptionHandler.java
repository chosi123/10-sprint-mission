package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BinaryContentNotFoundException.class, ChannelNotFoundException.class, UserNotFoundException.class, MessageNotFoundException.class})
    public ResponseEntity<?> NotFoundHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<?> WrongPasswordHandler(WrongPasswordException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("status", HttpStatus.UNAUTHORIZED.value(),
                        "message", e.getMessage())
        );
    }

    @ExceptionHandler(PrivateChannelUpdateException.class)
    public ResponseEntity<?> PrivateChannelUpdateHandler(PrivateChannelUpdateException e){
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                Map.of("status", HttpStatus.NOT_ACCEPTABLE.value(),
                        "message", e.getMessage())
        );
    }


}