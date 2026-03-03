package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.userstatus.UserStatusResponseMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    //
    private final UserStatusResponseMapper userStatusResponseMapper;

    @Override
    public UserStatusResponseDto create(UserStatusCreateRequestDto userStatusCreateRequestDto) {
        if(!userRepository.existsById(userStatusCreateRequestDto.userId())) throw new UserNotFoundException(userStatusCreateRequestDto.userId());
        if(userStatusRepository.findAll()
                .stream()
                .anyMatch(userStatus ->
                        userStatus.getUserId().equals(userStatusCreateRequestDto.userId())))
            throw new AssertionError("UserStatus already exists");

        UserStatus userstatus = userStatusRepository.save(new UserStatus(userStatusCreateRequestDto.userId()));
        return userStatusResponseMapper.toDto(userstatus);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new AssertionError("UserStatus not found"));
        return userStatusResponseMapper.toDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAll(UUID id) {
        return userStatusRepository.findAll().stream()
                .map(userStatusResponseMapper::toDto)
                .toList();
    }

    @Override
    public UserStatusResponseDto update(UUID id, UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new AssertionError("UserStatus not found"));

        userStatus.setLastOnlineTime(userStatusUpdateRequestDto.newLastActiveAt());

        userStatusRepository.save(userStatus);
        return userStatusResponseMapper.toDto(userStatus);
    }

    public UserStatusResponseDto updateByUserId(UUID id, UserStatusUpdateRequestDto userStatusUpdateRequestDto){
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                        .orElseThrow(() -> new AssertionError("UserStatus not found"));

        userStatus.setLastOnlineTime(userStatusUpdateRequestDto.newLastActiveAt());

        userStatusRepository.save(userStatus);

        return userStatusResponseMapper.toDto(userStatus);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }
}
