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
public interface ChannelResponseMapper {
    @Mapping(source = "lastMessageTime", target = "lastMessageAt")
    @Mapping(source = "participants", target = "participants")
    ChannelResponseDto toDto(Instant lastMessageTime, Channel channel, List<UserResponseDto> participants);

}
