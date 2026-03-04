package com.sprint.mission.discodeit.mapper.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.binarycontent.BinaryContentResponseMapper;
import com.sprint.mission.discodeit.mapper.userstatus.UserStatusResponseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentResponseMapper.class})
public interface UserResponseMapper {
    @Mapping(target = "online", expression = "java(user.getUserStatus().isOnline())")
    UserResponseDto toDto(User user);
}
