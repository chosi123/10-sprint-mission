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

    @Override
    public UserResponseDto create(UserCreateRequestDto userCreateRequestDto, MultipartFile profileImageFile) throws IOException {
        //중복여부 검사 로직
        if (userRepository.findAll().stream()
                .anyMatch(user ->
                        user.getUsername().equals(userCreateRequestDto.username()) ||
                                user.getEmail().equals(userCreateRequestDto.email())
                )) throw new IllegalArgumentException("Username or Email already exists");

        User user;
        //이미지 존재여부 분기
        if(profileImageFile != null){
            if(profileImageFile.getContentType() == null || !profileImageFile.getContentType().startsWith("image/"))
                throw new IllegalArgumentException("Invalid image file");

            BinaryContent profileImage = new BinaryContent(profileImageFile.getBytes(), profileImageFile.getContentType(), profileImageFile.getOriginalFilename(), profileImageFile.getSize());
            binaryContentRepository.save(profileImage);

            user = new User(userCreateRequestDto.username(),
                    userCreateRequestDto.email(),
                    userCreateRequestDto.password(),
                    profileImage.getId());
        }
        else user = new User(userCreateRequestDto.username(),
                userCreateRequestDto.email(),
                userCreateRequestDto.password(),
                null);

        //최종 저장
        userRepository.save(user);

        //userStatus 생성
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        //영속화
        if(profileImageFile != null && !profileImageFile.isEmpty()){
            String fileName = profileImageFile.getOriginalFilename();
            Path savePath = Paths.get("./upload/" + fileName);
            Files.createDirectories(savePath.getParent());
            profileImageFile.transferTo(savePath);
        }

        //저장된 데이터 리턴
        return userResponseMapper.toDto(user, userStatus);
    }

    @Override
    public UserResponseDto find(UUID userId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus targetUserStatus = userStatusRepository.findByUserId(targetUser.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + targetUser.getId() + " not found"));

        return userResponseMapper.toDto(targetUser, targetUserStatus);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<User> targetUsers = userRepository.findAll();

        return targetUsers.stream()
                .map(user -> find(user.getId()))
                .toList();
    }

    @Override
    public UserResponseDto update(UUID userId, UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImageFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow();

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
            if(user.getProfileId() != null){
                binaryContentRepository.deleteById(user.getProfileId());
            }
            newProfileImage = new BinaryContent(profileImageFile.getBytes(), profileImageFile.getContentType(), profileImageFile.getOriginalFilename(), profileImageFile.getSize());
            binaryContentRepository.save(newProfileImage);
            user.setProfileId(newProfileImage.getId());

            //영속화
            String fileName = profileImageFile.getOriginalFilename();
            Path savePath = Paths.get("./upload/" + fileName);
            Files.createDirectories(savePath.getParent());
            profileImageFile.transferTo(savePath);
        }
        if (anyValueUpdated) {
            user.isUpdated();
        }

        userRepository.save(user);

        return userResponseMapper.toDto(user, userStatus);
    }

    @Override
    public void delete(UUID userId) {
        //유저가 검색되지 않는 경우
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        User deletedUser = userRepository.findById(userId).get();
        binaryContentRepository.deleteById(deletedUser.getProfileId());//프로필 이미지 삭제

        UserStatus userStatus = userStatusRepository.findByUserId(userId).get();
        userStatusRepository.deleteById(userStatus.getId());//스테이터스 삭제

        userRepository.deleteById(userId);//유저레포지토리에서 삭제
    }
}
