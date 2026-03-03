package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.BaseUpdatableEntity;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.channel.ChannelResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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

    private final ChannelResponseMapper channelResponseMapper;

    private Instant findLastMessageTime(UUID channelId){
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(message -> message.getCreatedAt())
                .max(Instant::compareTo)
                .orElse(null);
    }

    @Override
    public Channel createPublicChannel(PublicChannelCreateRequestDto requestDto) {
        Channel channel = new Channel(PUBLIC, requestDto.name(), requestDto.description());
        channelRepository.save(channel);

        return channel;
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequestDto requestDto) {
        Channel channel = new Channel(PRIVATE, null, null);
        requestDto.participantIds()
                .forEach(user->{
                    channel.join(user);
                    readStatusRepository.save(new ReadStatus(user, channel.getId()));
                });

        channelRepository.save(channel);

        return channel;
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        return channelResponseMapper.toDto(findLastMessageTime(channelId), channel);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<ChannelResponseDto> dtoList = new ArrayList<>();

        channelRepository.findAll()
                .forEach(channel->{
                    if (channel.getType() == PUBLIC) {
                        dtoList.add(find(channel.getId()));
                    }
                    else if(channel.getParticipantIds().contains(userId)){
                        dtoList.add(find(channel.getId()));
                    }
        });

        return dtoList;
    }

    @Override
    public Channel update(UUID channelId, ChannelUpdateRequestDto updateRequestDto) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        if(channel.getType()==PRIVATE) throw new PrivateChannelUpdateException();

        channel.update(updateRequestDto.newName(), updateRequestDto.newDescription());

        channelRepository.save(channel);

        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(channelId);
        }

        //채널에 속한 메시지들 삭제
        messageRepository.findAll().stream()
                        .filter(message -> message.getChannelId().equals(channelId))
                        .map(BaseEntity::getId)
                        .forEach(messageRepository::deleteById);

        //채널을 갖고 있는 리드스테이터스 객체 삭제
        readStatusRepository.findAll().stream()
                        .filter(readstat -> readstat.getChannelID().equals(channelId))
                        .map(BaseEntity::getId)
                        .forEach(readStatusRepository::deleteById);

        //최종적으로, 채널 삭제
        channelRepository.deleteById(channelId);
    }
}
