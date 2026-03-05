package com.sprint.mission.discodeit.mapper.channel;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.user.UserResponseMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class})
public abstract class ChannelResponseMapper {
    @Autowired
    private ReadStatusRepository readStatusRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserResponseMapper userResponseMapper;

    @Mapping(source = "lastMessageTime", target = "lastMessageAt")
    @Mapping(target = "participants", expression = "java(getParticipants(channel))")
    public abstract ChannelResponseDto toDto(Instant lastMessageTime, Channel channel);

    protected List<UserResponseDto> getParticipants(Channel channel){
        return readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(r->userResponseMapper.toDto(r.getUser()))
                .toList();
    }
}
