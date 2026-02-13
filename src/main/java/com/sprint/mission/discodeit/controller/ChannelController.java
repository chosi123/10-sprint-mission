package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
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

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "채널 관련 API")
public class ChannelController {
    private final ChannelService channelService;

    @Operation(summary ="Public Channel 생성", operationId = "create_3")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "공개 채널 생성 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = Channel.class)
                    )
            )
    })
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublicChannel(
            @RequestBody PublicChannelCreateRequestDto requestDto
    ){
        return ResponseEntity.status(201).body(channelService.createPublicChannel(requestDto));
    }

    @Operation(summary ="Private Channel 생성", operationId = "create_4")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "비밀 채널 생성 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = Channel.class)
                    )
            )
    })
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequestDto requestDto
    ){
        return ResponseEntity.status(201).body(channelService.createPrivateChannel(requestDto));
    }

    @Operation(summary ="채널 수정", operationId = "update_3")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "채널 정보 수정 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = Channel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "채널 탐색 실패",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "Channel with id {channelId} not found"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Private 채널 수정 시도",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "Private channel cannot be updated"
                            )
                    )
            )
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Channel> updateChannel(@RequestBody ChannelUpdateRequestDto requestDto, @PathVariable UUID id){
        return ResponseEntity.status(200).body(channelService.update(id, requestDto));
    }

    @Operation(summary ="채널 삭제", operationId = "delete_2")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "채널 삭제 성공"
                    )
            ,
            @ApiResponse(
                    responseCode = "404",
                    description = "채널 탐색 실패",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "Channel with id {channelId} not found"
                            )
                    )
            )
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteChannel(@PathVariable UUID id){
        channelService.delete(id);
    }

    @Operation(summary ="유저가 참여중인 모든 채널 조회", operationId = "findAll_1")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저가 참여중인 모든 채널 조회",
                    content = @Content(
                            mediaType = "*/*",
                            array = @ArraySchema(schema = @Schema(implementation = ChannelResponseDto.class))
                    )
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponseDto>> getAllChannelWithUserId(
            @RequestParam UUID userId
    ){
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

}
