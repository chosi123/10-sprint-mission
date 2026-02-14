package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "유저 관련 API")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    //사용자 등록
    @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
    public UserResponseDto create(
            @RequestPart("userCreateRequest") UserCreateRequestDto dto,
            @RequestPart(value = "profile", required = false) MultipartFile profileImage
    ) throws IOException {
        //영속화
        if(profileImage != null && !profileImage.isEmpty()){
            String fileName = profileImage.getOriginalFilename();
            Path savePath = Paths.get("./upload/" + fileName);
            Files.createDirectories(savePath.getParent());
            profileImage.transferTo(savePath);
        }
        return userService.create(dto, profileImage);
    }

    //사용자 정보 수정
    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = "multipart/form-data")
    public UserResponseDto update(
            @PathVariable UUID userId,
            @RequestPart("dto") UserCreateRequestDto dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws IOException {
        if(profileImage != null && !profileImage.isEmpty()){
            String fileName = profileImage.getOriginalFilename();
            Path savePath = Paths.get("./upload/" + fileName);
            Files.createDirectories(savePath.getParent());
            profileImage.transferTo(savePath);
        }

        return userService.update(new UserUpdateRequestDto(userId, dto.username(), dto.email(), dto.password()), profileImage);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponseDto>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateUserStatusByUserId(@PathVariable UUID userId){
        userStatusService.updateByUserId(new UserStatusUpdateRequestDto(true, userId, Instant.now()));
        return ResponseEntity.noContent().build();
    }
}
