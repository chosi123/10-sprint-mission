package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth/login")
@Tag(name = "Auth", description = "로그인을 위한 API")
public class AuthController {
    private final AuthService authService;
    @Operation(
            summary ="로그인",
            operationId = "login"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = UserResponseDto.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "User with username {username} not found"
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "비밀번호가 일치하지 않음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "Wrong password"
                            )
                    )
            )
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto){
        return ResponseEntity.ok(authService.login(dto));
    }

}
