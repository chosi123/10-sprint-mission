package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationrepository;
  private final NotificationService notificationService;

  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  @Async("eventExecutor")
  public void on(MessageCreatedEvent event) {
    notificationService.createMessageNotifications(
        event.channelId(),
        event.senderId(),
        event.content()
    );
  }

  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  @Async("eventExecutor")
  public void on(RoleUpdatedEvent event) {
    notificationService.createRoleNotification(event.userId(), event.beforeRole(), event.afterRole());
  }

}
