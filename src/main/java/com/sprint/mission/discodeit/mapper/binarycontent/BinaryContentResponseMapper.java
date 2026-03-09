package com.sprint.mission.discodeit.mapper.binarycontent;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.channel.ChannelResponseMapper;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.AllArgsConstructor;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BinaryContentResponseMapper {
    BinaryContentResponseDto toDto(BinaryContent binaryContent);
}