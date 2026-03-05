package com.sprint.mission.discodeit.mapper.message;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.mapper.binarycontent.BinaryContentResponseMapper;
import com.sprint.mission.discodeit.mapper.user.UserResponseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BinaryContentResponseMapper.class, UserResponseMapper.class})
public interface MessageResponseMapper {
    @Mapping(target = "channelId", source = "channel.id")
    @Mapping(target = "attachments", source = "attachments")
    MessageResponseDto toDto(Message message);

    default BinaryContent map(MessageAttachment attachment){
        return attachment.getBinaryContent();
    }
}
