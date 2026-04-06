package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.channel.ChannelResponseMapper;
import com.sprint.mission.discodeit.mapper.user.UserResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;
import static com.sprint.mission.discodeit.entity.ChannelType.PUBLIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final ChannelResponseMapper channelResponseMapper;
    private final UserResponseMapper userResponseMapper;

    @Transactional
    @Override
    public ChannelResponseDto createPublicChannel(PublicChannelCreateRequestDto requestDto) {
        log.info("공개 채널 생성 시작");
        Channel channel = new Channel(PUBLIC, requestDto.name(), requestDto.description());
        channelRepository.save(channel);
        log.info("공개 채널 생성 완료");

        return channelResponseMapper.toDto(messageRepository.findLastMessageTimeWithChannelId(channel.getId()),
                channel,
                getParticpantsWithoutReadstatusList(channel));
    }

    @Override
    @Transactional
    public ChannelResponseDto createPrivateChannel(PrivateChannelCreateRequestDto requestDto) {
        log.info("비공개 채널 생성 시작");
        Channel channel = new Channel(PRIVATE, null, null);
        channelRepository.save(channel);

        requestDto.participantIds()
                .forEach(user-> readStatusRepository.save(new ReadStatus(userRepository.findById(user)
                        .orElseThrow(()->new UserNotFoundException(user)), channel, Instant.now())));

        log.info("비공개 채널 생성 완료");
        return channelResponseMapper.toDto(messageRepository.findLastMessageTimeWithChannelId(channel.getId()),
                channel,
                getParticpantsWithoutReadstatusList(channel));
    }

    @Transactional
    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        return channelResponseMapper.toDto(messageRepository.findLastMessageTimeWithChannelId(channelId),
                channel,
                getParticpantsWithoutReadstatusList(channel));
    }

    @Transactional
    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAllAccessibleChannelsByUserId(userId);

        List<UUID> channelIds = channels.stream()
                .map(Channel::getId)
                .toList();

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdIn(channelIds);

        Map<UUID, Instant> lastMessageTimes =
                messageRepository.findLastMessageTimes(channelIds)
                        .stream()
                        .collect(Collectors.toMap(
                                r -> (UUID) r[0],
                                r -> (Instant) r[1]
                        ));

        return channels.stream()
                .map(channel -> channelResponseMapper.toDto(
                        lastMessageTimes.get(channel.getId()),
                        channel,
                        getParticipants(channel, readStatuses)
                ))
                .toList();
    }

    @Transactional
    @Override
    public ChannelResponseDto update(UUID channelId, ChannelUpdateRequestDto updateRequestDto) {
        log.info("채널 정보 수정 시작");
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        if(channel.getType()==PRIVATE) throw new PrivateChannelUpdateException(channelId);

        channel.update(updateRequestDto.newName(), updateRequestDto.newDescription());

        channelRepository.save(channel);
        log.info("채널 정보 수정 완료");

        return channelResponseMapper.toDto(messageRepository.findLastMessageTimeWithChannelId(channel.getId()),
                channel,
                getParticpantsWithoutReadstatusList(channel));
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        log.info("채널 삭제 작업 시작");
        if (!channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(channelId);
        }
        log.info("채널 삭제 작업 완료");
        channelRepository.deleteById(channelId);
    }

    private List<UserResponseDto> getParticipants(Channel channel, List<ReadStatus> readStatuses){
        return readStatuses.stream()
                .filter(r->r.getChannel().getId().equals(channel.getId()))
                .map(r->userResponseMapper.toDto(r.getUser()))
                .toList();
    }

    private List<UserResponseDto> getParticpantsWithoutReadstatusList(Channel channel){
        return readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(r->userResponseMapper.toDto(r.getUser()))
                .toList();
    }
}
