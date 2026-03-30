package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

//메시지는 채널이 있어야 함.
@AllArgsConstructor
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "메시지 관련 API")
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "메시지 전송", operationId = "create_2")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "메시지 생성 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "유저 또는 채널을 찾을 수 없는 경우",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Channel | Author with id {channelId | authorId} not found")
                    )
            ),

    })
    @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<MessageResponseDto> postMessage(
            @RequestPart(value = "messageCreateRequest") MessageCreateRequestDto requestDto,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
            )throws IOException {
        return ResponseEntity.status(201).body(messageService.create(requestDto, attachments));
    }

    @Operation(summary = "메시지 수정", operationId = "update_2")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메시지 수정 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "메시지 검색 실패",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Message with id {messageId} not found")
                    )
            ),

    })
    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponseDto> patchMessage(
            @RequestBody MessageUpdateRequestDto requestDto,
            @PathVariable UUID messageId){
        return ResponseEntity.ok(messageService.update(messageId, requestDto, null));
    }

    @Operation(summary = "메시지 삭제", operationId = "delete_1")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "메시지 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "메시지 검색 실패",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("Message with id {messageId} not found")
                    )
            ),

    })
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public void deleteMessage(@PathVariable UUID messageId){
        messageService.delete(messageId);
    }

    @Operation(summary = "채널 내의 모든 메시지 출력", operationId = "findAllByChannelId")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "채널의 모든 메시지 가져오기",
                    content = @Content(
                            mediaType = "*/*",
                            array = @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class))
                    )
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public PageResponse<MessageResponseDto> getAllMessage(
            @RequestParam UUID channelId,
            @RequestParam(required = false) Instant cursor,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ){
        return messageService.findAllByChannelId(channelId, cursor, pageable);
    }

}
