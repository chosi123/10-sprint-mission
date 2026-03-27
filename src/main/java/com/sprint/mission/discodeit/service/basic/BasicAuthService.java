package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.WrongPasswordException;
import com.sprint.mission.discodeit.mapper.user.UserResponseMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserResponseMapper userResponseMapper;

    @Override
    @Transactional
    public UserResponseDto login(LoginRequestDto loginRequestDto) {
        User targetUser = userRepository.findByUsername(loginRequestDto.username())
                .orElseThrow(() -> new UserNotFoundException(loginRequestDto.username()));

        if(!targetUser.getPassword().equals(loginRequestDto.password())){
            throw new WrongPasswordException();
        }

        UserStatus userStatus = userStatusRepository.findByUserId(targetUser.getId())
                .get();
        userStatus.setLastActiveAt(Instant.now());
        userStatusRepository.save(userStatus);

        return userResponseMapper.toDto(targetUser);
    }
}
