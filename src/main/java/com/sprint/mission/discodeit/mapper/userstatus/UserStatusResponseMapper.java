package com.sprint.mission.discodeit.mapper.userstatus;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusResponseMapper {
    @Mapping(target = "userId", source = "user.id")
    UserStatusResponseDto toDto(UserStatus userStatus);
}
