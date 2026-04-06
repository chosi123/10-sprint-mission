package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.channel.ChannelResponseMapper;
import com.sprint.mission.discodeit.mapper.readstatus.ReadStatusResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    //
    private final ReadStatusResponseMapper readStatusResponseMapper;
    private final ChannelResponseMapper channelResponseMapper;

    @Override
    @Transactional
    public ReadStatusResponseDto create(ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        //못 찾으면 예외 발생시킴
        User user = userRepository.findById(readStatusCreateRequestDto.userId())
                .orElseThrow(()->new UserNotFoundException(readStatusCreateRequestDto.userId()));
        Channel channel = channelRepository.findById(readStatusCreateRequestDto.channelId())
                .orElseThrow(()->new ChannelNotFoundException(readStatusCreateRequestDto.channelId()));

        //이미 존재하면 예외
        if(readStatusRepository.existsByUserIdAndChannelId(readStatusCreateRequestDto.userId(), readStatusCreateRequestDto.channelId()))
        {
            return readStatusResponseMapper.toDto(readStatusRepository.findByUserIdAndChannelId(readStatusCreateRequestDto.userId(), readStatusCreateRequestDto.channelId()).get());
        }

        //생성 및 저장
        ReadStatus readStatus = readStatusRepository.save(new ReadStatus(user,
                channel, readStatusCreateRequestDto.lastReadAt()));

        return readStatusResponseMapper.toDto(readStatus);
    }

    @Override
    @Transactional
    public ReadStatusResponseDto find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new ReadStatusNotFoundException());
        return readStatusResponseMapper.toDto(readStatus);
    }

    public ReadStatusResponseDto findByUserIdAndMessageId(UUID userId, UUID messageId){

        Message message = messageRepository.findById(messageId)
                .orElseThrow();

        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(userId, message.getChannel().getId())
                .orElseThrow(() -> new ReadStatusNotFoundException());

        return readStatusResponseMapper.toDto(readStatus);
    }

    @Override
    @Transactional
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusResponseMapper::toDto)
                .toList();
    }


    @Transactional
    @Override
    public ReadStatusResponseDto update(UUID id, ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        ReadStatus targetReadStatus = readStatusRepository.findById(id)
                        .orElseThrow(()-> new ReadStatusNotFoundException(id));

        targetReadStatus.setLastReadAt(readStatusUpdateRequestDto.newLastReadAt());

        readStatusRepository.save(targetReadStatus);

        return readStatusResponseMapper.toDto(targetReadStatus);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }
}
