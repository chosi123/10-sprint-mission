package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
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
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponseDto> createPublicChannel(
            @RequestBody PublicChannelCreateRequestDto requestDto
    ){
        return ResponseEntity.status(201).body(channelService.createPublicChannel(requestDto));
    }

    @Operation(summary ="Private Channel 생성", operationId = "create_4")
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequestDto requestDto
    ){
        return ResponseEntity.status(201).body(channelService.createPrivateChannel(requestDto));
    }

    @Operation(summary ="채널 수정", operationId = "update_3")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelResponseDto> updateChannel(@RequestBody PublicChannelCreateRequestDto requestDto, @PathVariable UUID id){
        return ResponseEntity.status(204).body(channelService.update(new ChannelUpdateRequestDto(id,
                requestDto.name(), requestDto.description())));
    }

    @Operation(summary ="채널 삭제", operationId = "delete_2")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteChannel(@PathVariable UUID id){
        channelService.delete(id);
    }

    @Operation(summary ="유저가 참여중인 모든 채널 조회", operationId = "findAll_1")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponseDto>> getAllChannelWithUserId(
            @RequestParam UUID userId
    ){
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

}
