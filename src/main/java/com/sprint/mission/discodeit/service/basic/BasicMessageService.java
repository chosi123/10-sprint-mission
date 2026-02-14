package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.message.MessageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    //
    private final MessageResponseMapper messageResponseMapper;

    @SneakyThrows
    @Override
    public MessageResponseDto create(MessageCreateRequestDto messageCreateRequestDto, List<MultipartFile> files) {
        if (!channelRepository.existsById(messageCreateRequestDto.channelId())) {
            throw new ChannelNotFoundException(messageCreateRequestDto.channelId());
        }
        if (!userRepository.existsById(messageCreateRequestDto.authorId())) {
            throw new UserNotFoundException("Author not found with id " + messageCreateRequestDto.authorId());
        }

        List<MultipartFile> safeFiles = Optional.ofNullable(files).orElse(List.of());

        List<UUID> attachmentListToId = safeFiles.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> {
                    try {
                        BinaryContent b = new BinaryContent(file.getBytes());
                        binaryContentRepository.save(b);
                        return b.getId();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read multipart file", e);
                    }
                })
                .toList();

        Message message = new Message(
                messageCreateRequestDto.content(),
                messageCreateRequestDto.channelId(),
                messageCreateRequestDto.authorId(),
                attachmentListToId
        );
        messageRepository.save(message);

        return messageResponseMapper.toDto(message);
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message targetMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        return messageResponseMapper.toDto(targetMessage);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .sorted((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt()))
                .toList();

        return messages.stream()
                .map(messageResponseMapper::toDto)
                .toList();
    }

    @Override
    public MessageResponseDto update(
            UUID id,
            MessageUpdateRequestDto requestDto, List<MultipartFile> files
    ) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() ->
                        new MessageNotFoundException(id)
                );

        boolean hasNewFiles =
                files != null &&
                        files.stream().anyMatch(f -> f != null && !f.isEmpty());

        List<UUID> newAttachmentIds = null;

        if (hasNewFiles) {
            // 1️⃣ 기존 첨부 삭제
            for (UUID oldId : message.getAttachments()) {
                binaryContentRepository.deleteById(oldId);
            }

            // 2️⃣ 새 첨부 저장
            newAttachmentIds = files.stream()
                    .filter(f -> f != null && !f.isEmpty())
                    .map(f -> {
                        try {
                            BinaryContent saved =
                                    binaryContentRepository.save(new BinaryContent(f.getBytes()));
                            return saved.getId();
                        } catch (IOException e) {
                            throw new ResponseStatusException(
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    "Failed to read attachment",
                                    e
                            );
                        }
                    })
                    .toList();
        }

        // 3️⃣ 업데이트
        // 👉 새 파일이 없으면 attachments는 건드리지 않음
        message.update(
                requestDto.newContent(),
                hasNewFiles ? newAttachmentIds : message.getAttachments()
        );

        messageRepository.save(message);

        return messageResponseMapper.toDto(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new MessageNotFoundException(messageId);
        }

        //내부 파일들 바이너리레포에서 삭제
        messageRepository.findById(messageId).orElseThrow()
                .getAttachments().forEach(binaryContentRepository::deleteById);

        // 메시지레포에서 삭제
        messageRepository.deleteById(messageId);
    }
}
