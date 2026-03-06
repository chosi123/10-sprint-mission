package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageCreateRequestDto messageCreateRequestDto, List<MultipartFile> files);
    MessageResponseDto find(UUID messageId);
    PageResponse<MessageResponseDto> findAllByChannelId(UUID channelId, Pageable pageable);
    MessageResponseDto update(UUID id, MessageUpdateRequestDto requestDto, List<MultipartFile> files);
    void delete(UUID messageId);
}
