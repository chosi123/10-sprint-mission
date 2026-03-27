package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.*;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/readStatuses")
@Tag(name = "ReadStatus", description = "메시지의 확인 여부와 관련된 API")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @Operation(summary = "메시지 확인 여부 객체 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "객체 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = ReadStatusResponseDto.class)
    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "대상 유저를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "User with username {username} not found"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "대상 채널을 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "Channel with {channelid} not found"
                            )
                    )
            )
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponseDto> create_1(
            @RequestBody @Valid ReadStatusCreateRequestDto dto
    ){
        return ResponseEntity.status(201).body(readStatusService.create(dto));
    }

    @Operation(summary = "메시지 확인 여부 갱신")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메시지 확인 여부 갱신 성공",
                    content = @Content(
                            schema = @Schema(implementation = ReadStatusResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "대상 객체를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "ReadStatus with {readStatusId} not found"
                            )
                    )
            )
    })
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponseDto> update_1(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequestDto requestDto
    ){
        return ResponseEntity.ok(readStatusService.update(readStatusId, requestDto));
    }

    @Operation(summary = "메시지 확인 여부 가져오기")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메시지 확인 여부 갱신 성공",
                    content = @Content(
                            schema = @Schema(implementation = ReadStatusResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "대상 객체를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "ReadStatus with {readStatusId} not found"
                            )
                    )
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponseDto>> getReadStatus(
            @RequestParam UUID userId
            ){

        List<ReadStatusResponseDto> dtoList = readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(dtoList);
    }

    /*
    @RequestMapping(value = "/{userId}/readstatus")
    public List<IsMessageReadResponseDto> getAllReadStatus(@PathVariable UUID userId){
        return readStatusService.findAllByUserId(userId);
    }
     */

}
