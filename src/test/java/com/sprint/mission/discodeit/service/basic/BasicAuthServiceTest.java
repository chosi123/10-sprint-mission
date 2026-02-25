package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.LoginResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.user.LoginResponseMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private LoginResponseMapper loginResponseMapper;

    @InjectMocks
    private BasicAuthService basicAuthService;

    @Test
    void login_createsUserStatusWhenMissing() {
        User user = new User("tester", "tester@example.com", "password", null);
        UserStatus createdStatus = new UserStatus(user.getId());
        LoginResponseDto responseDto = new LoginResponseDto(user.getId(), user.getUsername(), user.getEmail());

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userStatusRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(userStatusRepository.save(any(UserStatus.class))).thenReturn(createdStatus);
        when(loginResponseMapper.toDto(user)).thenReturn(responseDto);

        LoginResponseDto result = basicAuthService.login(new LoginRequestDto("tester", "password"));

        assertThat(result).isEqualTo(responseDto);
        verify(userStatusRepository, times(2)).save(any(UserStatus.class));
        verify(loginResponseMapper).toDto(user);
    }

    @Test
    void login_updatesExistingUserStatusWhenPresent() {
        User user = new User("tester", "tester@example.com", "password", null);
        UserStatus existingStatus = new UserStatus(user.getId());
        LoginResponseDto responseDto = new LoginResponseDto(user.getId(), user.getUsername(), user.getEmail());

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userStatusRepository.findByUserId(user.getId())).thenReturn(Optional.of(existingStatus));
        when(userStatusRepository.save(existingStatus)).thenReturn(existingStatus);
        when(loginResponseMapper.toDto(user)).thenReturn(responseDto);

        LoginResponseDto result = basicAuthService.login(new LoginRequestDto("tester", "password"));

        assertThat(result).isEqualTo(responseDto);
        verify(userStatusRepository, never()).save(argThat(status -> !status.getId().equals(existingStatus.getId())));
        verify(userStatusRepository).save(existingStatus);
    }
}
