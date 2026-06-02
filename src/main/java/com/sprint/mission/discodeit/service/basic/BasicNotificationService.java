package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  @Transactional(readOnly = true)
  public List<NotificationDto> findAllNotification(UUID receiverId) {
    List<Notification> notifications = notificationRepository.findByReceiverId(receiverId)
        .orElse(List.of());

    return notifications.stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  @PreAuthorize("@basicNotificationService.isOwner(#notificationId, #authId)")
  public void deleteNotification(UUID notificationId, UUID authId) {
    if (!notificationRepository.existsById(notificationId)) {
      throw new NotificationNotFoundException();
    }
    notificationRepository.deleteById(notificationId);
  }

  public boolean isOwner(UUID notificationId, UUID receiverId) {
    Notification n = notificationRepository.findById(notificationId).orElseThrow(NotificationNotFoundException::new);
    return n.getReceiverId().equals(receiverId);
  }
}
