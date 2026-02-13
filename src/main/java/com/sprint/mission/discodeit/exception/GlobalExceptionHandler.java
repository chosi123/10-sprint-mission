package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> UserNotFoundHandler(UserNotFoundException e) {
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

    @ExceptionHandler(BinaryContentNotFoundException.class)
    public ResponseEntity<?> BinaryContentNotFoundHandler(BinaryContentNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("status", HttpStatus.NOT_FOUND.value(),
                        "message", e.getMessage())
        );
    }
}