package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.WrongImageException;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.user.UserResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserResponseMapper userResponseMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateRequestDto userCreateRequestDto, MultipartFile profileImageFile) {
        log.debug("회원 생성 시작: username: {}, email: {}", userCreateRequestDto.username(), userCreateRequestDto.email());

        //중복여부 검사 로직
        if (userRepository.existsByUsername(userCreateRequestDto.username())){
            throw new UserNameAlreadyExistException(userCreateRequestDto.username());
        }
        if (userRepository.existsByEmail(userCreateRequestDto.email())){
            throw new EmailAlreadyExistException(userCreateRequestDto.email());
        }

        User user;

        //이미지 존재여부 분기
        if(profileImageFile != null){
            if(profileImageFile.getContentType() == null || !profileImageFile.getContentType().startsWith("image/")){
                throw new WrongImageException(profileImageFile.getOriginalFilename(), profileImageFile.getContentType());
            }

            BinaryContent profileImage = new BinaryContent(profileImageFile.getContentType(), profileImageFile.getOriginalFilename(), profileImageFile.getSize());
            binaryContentRepository.save(profileImage);
            binaryContentStorage.put(profileImage.getId(), profileImageFile.getBytes());

            try{
                binaryContentStorage.put(profileImage.getId(), profileImageFile.getBytes());

            }catch (IOException e){
                throw new WrongImageException(profileImageFile.getOriginalFilename(), profileImageFile.getContentType());
            }

            user = new User(userCreateRequestDto.username(),
                    userCreateRequestDto.email(),
                    userCreateRequestDto.password(),
                    profileImage);
        }
        else user = new User(userCreateRequestDto.username(),
                userCreateRequestDto.email(),
                userCreateRequestDto.password(),
                null);
        UserStatus userStatus = new UserStatus();
        user.setUserStatus(userStatus);

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

        BinaryContent newProfileImage;

        boolean anyValueUpdated = false;
        if (userUpdateRequestDto.newUsername() != null && !userUpdateRequestDto.newUsername().equals(user.getUsername())) {
            //수정 시에도 중복 방지
            if(userRepository.existsByUsername(userUpdateRequestDto.newUsername())){
                throw new UserNameAlreadyExistException(userUpdateRequestDto.newUsername());
            }

            user.setUsername(userUpdateRequestDto.newUsername());
            anyValueUpdated = true;
        }
        if (userUpdateRequestDto.newEmail() != null && !userUpdateRequestDto.newEmail().equals(user.getEmail())) {
            //수정 시에도 중복 방지
            if(userRepository.existsByEmail(userUpdateRequestDto.newEmail())){
                throw new EmailAlreadyExistException(userUpdateRequestDto.newEmail());
            }

            user.setEmail(userUpdateRequestDto.newEmail());
            anyValueUpdated = true;
        }
        if (userUpdateRequestDto.newPassword() != null && !userUpdateRequestDto.newPassword().equals(user.getPassword())) {
            user.setPassword(userUpdateRequestDto.newPassword());
            anyValueUpdated = true;
        }
        if(profileImageFile != null && !profileImageFile.isEmpty()){
            //제공된 파일 타입 유효성 검증
            if(profileImageFile.getContentType() == null || !profileImageFile.getContentType().startsWith("image/")){
                throw new WrongImageException(profileImageFile.getOriginalFilename(), profileImageFile.getContentType());
            }

            if(user.getProfile() != null){
                binaryContentRepository.deleteById(user.getProfile().getId());
            }
            newProfileImage = new BinaryContent(profileImageFile.getContentType(), profileImageFile.getOriginalFilename(), profileImageFile.getSize());
            binaryContentRepository.save(newProfileImage);
            binaryContentStorage.put(newProfileImage.getId(), profileImageFile.getBytes());
            user.setProfile(newProfileImage);

        }
        if (anyValueUpdated) {
            user.isUpdated();
        }

        userRepository.save(user);
        log.info("회원 정보 수정이 완료되었습니다: userId: {}, username: {}, email: {}", userId, user.getUsername(), user.getEmail());

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
        log.info("회원 id {}에 대한 회원 탈퇴 처리 완료", userId);
    }
}
