package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.user.UserResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserResponseMapper userResponseMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateRequestDto userCreateRequestDto, MultipartFile profileImageFile) throws IOException {
        //중복여부 검사 로직
        if (userRepository.existsByEmail(userCreateRequestDto.email())
                || userRepository.existsByUsername(userCreateRequestDto.username()))
            throw new IllegalArgumentException("Username and Email already exists");

        User user;

        //이미지 존재여부 분기
        if(profileImageFile != null){
            if(profileImageFile.getContentType() == null || !profileImageFile.getContentType().startsWith("image/"))
                throw new IllegalArgumentException("Invalid image file");

            BinaryContent profileImage = new BinaryContent(profileImageFile.getContentType(), profileImageFile.getOriginalFilename(), profileImageFile.getSize());
            binaryContentStorage.put(profileImage.getId(), profileImageFile.getBytes());
            binaryContentRepository.save(profileImage);

            user = new User(userCreateRequestDto.username(),
                    userCreateRequestDto.email(),
                    userCreateRequestDto.password(),
                    profileImage);
        }
        else user = new User(userCreateRequestDto.username(),
                userCreateRequestDto.email(),
                userCreateRequestDto.password(),
                null);

        //userStatus 생성
        UserStatus userStatus = new UserStatus(user);
        user.setUserStatus(userStatus);
        userStatusRepository.save(userStatus);

        //최종 저장
        userRepository.save(user);

        //저장된 데이터 리턴
        return userResponseMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserResponseDto find(UUID userId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return userResponseMapper.toDto(targetUser);
    }

    @Transactional
    @Override
    public List<UserResponseDto> findAll() {
        List<User> targetUsers = userRepository.findAll();

        return targetUsers.stream()
                .map(userResponseMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserResponseDto update(UUID userId, UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImageFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserStatus userStatus = user.getUserStatus();

        BinaryContent newProfileImage;

        boolean anyValueUpdated = false;
        if (userUpdateRequestDto.newUsername() != null && !userUpdateRequestDto.newUsername().equals(user.getUsername())) {
            user.setUsername(userUpdateRequestDto.newUsername());
            anyValueUpdated = true;
        }
        if (userUpdateRequestDto.newEmail() != null && !userUpdateRequestDto.newEmail().equals(user.getEmail())) {
            user.setEmail(userUpdateRequestDto.newEmail());
            anyValueUpdated = true;
        }
        if (userUpdateRequestDto.newPassword() != null && !userUpdateRequestDto.newPassword().equals(user.getPassword())) {
            user.setPassword(userUpdateRequestDto.newPassword());
            anyValueUpdated = true;
        }
        if(profileImageFile != null && !profileImageFile.isEmpty()){
            if(user.getProfile() != null){
                binaryContentRepository.deleteById(user.getProfile().getId());
            }
            newProfileImage = new BinaryContent(profileImageFile.getContentType(), profileImageFile.getOriginalFilename(), profileImageFile.getSize());
            binaryContentRepository.save(newProfileImage);
            user.setProfile(newProfileImage);

        }
        if (anyValueUpdated) {
            user.isUpdated();
        }

        userRepository.save(user);

        return userResponseMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        //유저가 검색되지 않는 경우
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        userRepository.deleteById(userId);//유저레포지토리에서 삭제
    }
}
