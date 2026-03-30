package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.binarycontent.WrongImageException;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistException;
import com.sprint.mission.discodeit.mapper.binarycontent.BinaryContentResponseMapper;
import com.sprint.mission.discodeit.mapper.user.UserResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    MockMultipartFile rightFile =
            new MockMultipartFile(
                    "profileImage",        // 파라미터 이름
                    "profile.png",         // 원본 파일명
                    "image/png",           // Content-Type
                    "hello".getBytes()     // 파일 내용
            );

    MockMultipartFile wrongFile =
            new MockMultipartFile(
                    "profileImage",        // 파라미터 이름
                    "profile.png",         // 원본 파일명
                    "json",           // Content-Type
                    "hello".getBytes()     // 파일 내용
            );

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private UserResponseMapper userResponseMapper =
            Mappers.getMapper(UserResponseMapper.class);

    @Mock
    private BinaryContentResponseMapper binaryContentResponseMapper =
            Mappers.getMapper(BinaryContentResponseMapper.class);

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("정상적인 사용자 생성 요청이면 사용자가 저장되고 DTO가 반환된다")
    void createUser_success() {
        // given
        when(userRepository.existsByUsername("초시")).thenReturn(false);
        when(userRepository.existsByEmail("f@g.com")).thenReturn(false);
        when(binaryContentRepository.save(any(BinaryContent.class)))
                .thenAnswer(invocation -> {
                    BinaryContent bc = invocation.getArgument(0);
                    ReflectionTestUtils.setField(bc, "id", UUID.randomUUID());
                    return bc;
                });

        when(binaryContentStorage.put(any(UUID.class), any(byte[].class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userResponseMapper.toDto(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    return new UserResponseDto(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            new BinaryContentResponseDto(
                                    user.getProfile().getId(),
                                    user.getProfile().getFileName(),
                                    user.getProfile().getSize(),
                                    user.getProfile().getContentType()
                            ),
                            false
                    );
                });

        // when
        UserResponseDto response = userService.create(
                new UserCreateRequestDto("초시", "f@g.com", "password"),
                rightFile
        );

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("초시", savedUser.getUsername());
        assertEquals("f@g.com", savedUser.getEmail());
        assertEquals("image/png", savedUser.getProfile().getContentType());

        assertEquals("초시", response.username());
    }

    @Test
    @DisplayName("사용자 이름이 중복되면 유저가 생성되지 않는다")
    void createUser_fail_duplicateUsername() {
        //given
        when(userRepository.existsByUsername("이도윤")).thenReturn(true);

        //when
        UserNameAlreadyExistException ex = assertThrows(UserNameAlreadyExistException.class, () -> userService.create(
                new UserCreateRequestDto("이도윤", "f@g.com", "password"),
                null));

        //then
        assertEquals("이미 존재하는 사용자 이름입니다.", ex.getMessage());
    }

    @Test
    @DisplayName("이메일이 중복되면 유저가 생성되지 않는다")
    void createUser_fail_duplicateEmail() {
        //given
        when(userRepository.existsByUsername("나로")).thenReturn(false);
        when(userRepository.existsByEmail("f@g.com")).thenReturn(true);

        //when
        EmailAlreadyExistException ex = assertThrows(EmailAlreadyExistException.class, () -> userService.create(
                new UserCreateRequestDto("나로", "f@g.com", "password"),
                null));

        //then
        assertEquals("이미 존재하는 이메일입니다.", ex.getMessage());
    }

    @Test
    @DisplayName("이미지가 아닌 파일이 프로필 이미지로 들어오면 회원이 생성되지 않는다")
    void createUser_fail_wrongFile() {
        //given
        when(userRepository.existsByUsername("초시")).thenReturn(false);
        when(userRepository.existsByEmail("f@g.com")).thenReturn(false);

        //when
        WrongImageException ex = assertThrows(WrongImageException.class, () -> userService.create(
                new UserCreateRequestDto("초시", "f@g.com", "password"),
                wrongFile));

        //then
        assertEquals("이미지 파일이 아닌 파일은 프로필 이미지로 사용할 수 없습니다.", ex.getMessage());
    }

//    @Test
//    @DisplayName("회원 정상 수정 테스트")
//    void updateUser_success() {
//        //given
//        when(userRepository.existsById(any(UUID.class))).thenReturn(true);
//        when(userRepository.save(any(User.class))).thenReturn(new User());
//        UserUpdateRequestDto dto = new UserUpdateRequestDto(
//
//        );
//
//        //when
//        userService.update()
//    }
}
