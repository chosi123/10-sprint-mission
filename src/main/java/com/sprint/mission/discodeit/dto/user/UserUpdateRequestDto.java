package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserUpdateRequestDto(
        String newUsername,
        String newEmail,
        String newPassword
) {
}
