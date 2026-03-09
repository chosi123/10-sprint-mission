package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.userstatus.UserStatusResponseMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.transaction.Transactional;
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
    @Transactional
    public UserStatusResponseDto create(UserStatusCreateRequestDto userStatusCreateRequestDto) {
        User user = userRepository.findById(userStatusCreateRequestDto.userId())
                .orElseThrow(() -> new UserNotFoundException(userStatusCreateRequestDto.userId()));

        UserStatus userstatus = new UserStatus();
        userstatus.setUser(user);
        userStatusRepository.save(userstatus);
        return userStatusResponseMapper.toDto(userstatus);
    }

    @Override
    @Transactional
    public UserStatusResponseDto find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new AssertionError("UserStatus not found"));
        return userStatusResponseMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public List<UserStatusResponseDto> findAll(UUID id) {
        return userStatusRepository.findAll().stream()
                .map(userStatusResponseMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserStatusResponseDto update(UUID id, UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new AssertionError("UserStatus not found"));

        userStatus.setLastActiveAt(userStatusUpdateRequestDto.newLastActiveAt());

        userStatusRepository.save(userStatus);
        return userStatusResponseMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public UserStatusResponseDto updateByUserId(UUID id, UserStatusUpdateRequestDto userStatusUpdateRequestDto){
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                        .orElseThrow(() -> new AssertionError("UserStatus not found"));

        userStatus.setLastActiveAt(userStatusUpdateRequestDto.newLastActiveAt());

        userStatusRepository.save(userStatus);

        return userStatusResponseMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }
}
