package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

//메시지는 채널이 있어야 함.
@AllArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public MessageResponseDto postMessage(
            @RequestPart("dto") MessageCreateRequestDto requestDto,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
            )throws IOException {

        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                if (file == null || file.isEmpty()) continue;

                String fileName = file.getOriginalFilename();
                Path savePath = Paths.get("./upload/" + fileName);
                Files.createDirectories(savePath.getParent());
                file.transferTo(savePath);
            }
        }

        return messageService.create(requestDto, attachments);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public MessageResponseDto patchMessage(
            @RequestPart("dto") MessageUpdateRequestDto requestDto,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
            @PathVariable UUID messageId){
        return messageService.update(messageId, requestDto, attachments);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public void deleteMessage(@PathVariable UUID messageId){
        messageService.delete(messageId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponseDto> getAllMessage(@RequestParam UUID channelId){
        return messageService.findAllByChannelId(channelId);
    }

}
