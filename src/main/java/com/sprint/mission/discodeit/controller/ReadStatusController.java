package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.*;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ReadStatusResponseDto createReadStatus(
            @RequestBody ReadStatusCreateRequestDto dto
    ){
        return readStatusService.create(dto);
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ReadStatusResponseDto updateReadStatus(
            @PathVariable UUID readStatusId
    ){
        ReadStatusResponseDto responseDto = readStatusService.find(readStatusId);
        return readStatusService.update(new ReadStatusUpdateRequestDto(responseDto.userId(), responseDto.channelId(), Instant.now()));
    }

    @RequestMapping(method = RequestMethod.GET)
    public IsMessageReadResponseDto getReadStatus(
            @RequestBody IsMessageReadRequestDto dto
            ){
        return readStatusService.findByUserIdAndMessageId(dto.userId(), dto.messageId());
    }

    /*
    @RequestMapping(value = "/{userId}/readstatus")
    public List<IsMessageReadResponseDto> getAllReadStatus(@PathVariable UUID userId){
        return readStatusService.findAllByUserId(userId);
    }
     */

}
