package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.LoginResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.WrongPasswordException;
import com.sprint.mission.discodeit.mapper.user.LoginResponseMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final LoginResponseMapper loginResponseMapper;

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        List<User> userList = userRepository.findAll();

        User targetUser = userList.stream()
                .filter(user -> user.getUsername().equals(loginRequestDto.username()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(loginRequestDto.username()));

        if(!targetUser.getPassword().equals(loginRequestDto.password())){
            throw new WrongPasswordException();
        }

        UserStatus userStatus = userStatusRepository.findByUserId(targetUser.getId())
                .get();
        userStatus.setLastActiveAt(Instant.now());
        userStatusRepository.save(userStatus);

        return loginResponseMapper.toDto(targetUser);
    }
}
