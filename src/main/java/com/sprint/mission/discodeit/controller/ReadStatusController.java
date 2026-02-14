package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.*;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/readStatuses")
@Tag(name = "ReadStatus", description = "메시지의 확인 여부와 관련된 API")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponseDto> createReadStatus(
            @RequestBody ReadStatusCreateRequestDto dto
    ){
        return ResponseEntity.status(201).body(readStatusService.create(dto));
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponseDto> update_1(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequestDto requestDto
    ){
        return ResponseEntity.ok(readStatusService.update(readStatusId, requestDto));
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
