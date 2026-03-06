package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.channel.ChannelResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;
import static com.sprint.mission.discodeit.entity.ChannelType.PUBLIC;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final ChannelResponseMapper channelResponseMapper;

    @Transactional
    @Override
    public ChannelResponseDto createPublicChannel(PublicChannelCreateRequestDto requestDto) {
        Channel channel = new Channel(PUBLIC, requestDto.name(), requestDto.description());
        channelRepository.save(channel);

        return channelResponseMapper.toDto(messageRepository.findLastMessageTimeWithChannelId(channel.getId()), channel);
    }

    @Override
    @Transactional
    public ChannelResponseDto createPrivateChannel(PrivateChannelCreateRequestDto requestDto) {
        Channel channel = new Channel(PRIVATE, null, null);
        requestDto.participantIds()
                .forEach(user-> readStatusRepository.save(new ReadStatus(userRepository.findById(user)
                        .orElseThrow(()->new UserNotFoundException(user)), channel)));

        channelRepository.save(channel);

        return channelResponseMapper.toDto(messageRepository.findLastMessageTimeWithChannelId(channel.getId()), channel);
    }

    @Transactional
    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        return channelResponseMapper.toDto(messageRepository.findLastMessageTimeWithChannelId(channelId), channel);
    }

    @Transactional
    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        return channelRepository.findAllAccessibleChannelsByUserId(userId).stream()
                .map(channel -> channelResponseMapper.toDto(
                        messageRepository.findLastMessageTimeWithChannelId(channel.getId()), channel))
                .toList();
    }

    @Transactional
    @Override
    public ChannelResponseDto update(UUID channelId, ChannelUpdateRequestDto updateRequestDto) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        if(channel.getType()==PRIVATE) throw new PrivateChannelUpdateException();

        channel.update(updateRequestDto.newName(), updateRequestDto.newDescription());

        channelRepository.save(channel);

        return channelResponseMapper.toDto(messageRepository.findLastMessageTimeWithChannelId(channel.getId()), channel);
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(channelId);
        }

        channelRepository.deleteById(channelId);
    }
}
