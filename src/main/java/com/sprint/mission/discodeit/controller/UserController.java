package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Operation(summary = "유저 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "유저 생성 성공",
                    content = @Content(
                            mediaType = "image/*",
                            schema = @Schema(implementation = UserResponseDto.class)
                    )
            )
    })
    @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDto> create(
            @RequestPart("userCreateRequest") UserCreateRequestDto dto,
            @RequestPart(value = "profile", required = false) MultipartFile profileImage
    ) throws IOException {
        return ResponseEntity.status(201).body(userService.create(dto, profileImage));
    }

    //사용자 정보 수정
    @Operation(summary = "유저 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 수정 성공",
                    content = @Content(
                            mediaType = "image/*",
                            schema = @Schema(implementation = UserResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "유저를 찾을 수 없음",
                    content = @Content(
                            mediaType = "image/*",
                            examples = @ExampleObject(
                                    value = "User with username {username} not found"
                            )
                    )
            )
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = "multipart/form-data")
    public UserResponseDto update(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequestDto userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profileImage
    ) throws IOException {
        return userService.update(userId, userUpdateRequest, profileImage);
    }

    @Operation(summary = "유저 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "유저 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "대상 유저를 찾을 수 없음",
                    content = @Content(
                            mediaType = "image/*",
                            examples = @ExampleObject(
                                    value = "User with username {username} not found"
                            )
                    )
            )
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    @Operation(summary = "가입한 모든 유저 반환")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "모든 유저 반환",
                    content = @Content(
                            mediaType = "image/*",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class))
                    )
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponseDto>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "유저 접속 상태 업데이트")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "유저 접속 상태 업데이트 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "대상 유저를 찾을 수 없음",
                    content = @Content(
                            mediaType = "image/*",
                            examples = @ExampleObject(
                                    value = "User with username {username} not found"
                            )
                    )
            )
    })
    @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateUserStatusByUserId(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequestDto dto
    ){
        userStatusService.updateByUserId(userId, dto);
        return ResponseEntity.noContent().build();
    }
}
