package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.binarycontent.FileStorageException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.mapper.message.MessageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    //
    private final MessageResponseMapper messageResponseMapper;
    private final PageResponseMapper pageResponseMapper;

    @SneakyThrows
    @Override
    @Transactional
    public MessageResponseDto create(MessageCreateRequestDto messageCreateRequestDto, List<MultipartFile> files) {
        log.info("메시지 전송 작업을 시작합니다.");

        Channel channel = channelRepository.findById(messageCreateRequestDto.channelId())
                .orElseThrow(() -> new ChannelNotFoundException(messageCreateRequestDto.channelId()));
        User user = userRepository.findById(messageCreateRequestDto.authorId())
                .orElseThrow(() -> new UserNotFoundException(messageCreateRequestDto.authorId()));

        Message message = new Message(
                messageCreateRequestDto.content(),
                channel,
                user,
                new ArrayList<>()
        );

        List<MultipartFile> safeFiles = Optional.ofNullable(files).orElse(List.of());

        safeFiles.stream()
                .filter(file -> file != null && !file.isEmpty())
                .forEach(file ->
                        message.getAttachments().add(new MessageAttachment(message, toBinaryContent(file))));

        Message savedMessage = messageRepository.save(message);
        log.info("메시지 전송 작업이 완료되었습니다.");
        return messageResponseMapper.toDto(savedMessage);
    }


    @Override
    @Transactional
    public MessageResponseDto find(UUID messageId) {
        Message targetMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        return messageResponseMapper.toDto(targetMessage);
    }

    @Override
    @Transactional
    public PageResponse<MessageResponseDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        Slice<Message> messages;

        if(cursor == null){
            messages = messageRepository.findByChannelId(channelId, pageable);
        }
        else{
            messages = messageRepository.findByChannelIdAndCreatedAtLessThan(channelId, cursor, pageable);
        }

        return pageResponseMapper.fromSlice(messages.map(messageResponseMapper::toDto));
    }

    @Override
    @Transactional
    public MessageResponseDto update(
            UUID id,
            MessageUpdateRequestDto requestDto, List<MultipartFile> files
    ) {
        log.info("메시지 수정 작업을 시작합니다.");
        Message message = messageRepository.findById(id)
                .orElseThrow(() ->
                        new MessageNotFoundException(id)
                );

        boolean hasNewFiles =
                files != null &&
                        files.stream().anyMatch(f -> f != null && !f.isEmpty());

        List<MessageAttachment> newAttachments = null;

        if (hasNewFiles) {
            // 1️⃣ 기존 첨부 삭제
            for (MessageAttachment old : message.getAttachments()) {
                binaryContentRepository.deleteById(old.getBinaryContent().getId());
            }

            // 2️⃣ 새 첨부 저장
            newAttachments = files.stream()
                    .filter(f -> f != null && !f.isEmpty())
                    .map(f -> new MessageAttachment(message, toBinaryContent(f)))
                    .toList();
        }

        // 3️⃣ 업데이트
        // 👉 새 파일이 없으면 attachments는 건드리지 않음
        message.update(
                requestDto.newContent(),
                hasNewFiles ? newAttachments : message.getAttachments()
        );

        messageRepository.save(message);
        log.info("메시지 수정 작업이 완료되었습니다.");
        return messageResponseMapper.toDto(message);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        log.info("메시지 삭제 작업을 시작합니다. messageId: {}", messageId);
        if (!messageRepository.existsById(messageId)) {
            throw new MessageNotFoundException(messageId);
        }

        // 메시지레포에서 삭제
        messageRepository.deleteById(messageId);
        log.info("메시지 삭제 작업이 완료되었습니다.");
    }

    private BinaryContent toBinaryContent(MultipartFile file) {
        try {
            BinaryContent binaryContent = new BinaryContent(file.getContentType(), file.getOriginalFilename(), file.getSize());
            binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(binaryContent.getId(), file.getBytes());
            return binaryContent;
        } catch (IOException e) {
            throw new FileStorageException();
        }
    }

}
