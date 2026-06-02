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
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationrepository;

  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  public void on(MessageCreatedEvent event) {
    List<ReadStatus> list = readStatusRepository.findAllByChannelIdAndUserIdNotAndNotificationEnabled(event.channelId(), event.senderId(), true);

    list.forEach(
        readStatus ->
          notificationrepository.save(new Notification(
              readStatus.getUser().getUsername() + " (#" + readStatus.getChannel().getName() + ")",
              event.content(),
              readStatus.getUser().getId()
              )
          )
    );
  }

  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  public void on(RoleUpdatedEvent event) {
    notificationrepository.save(
        new Notification(
            "권한이 변경되었습니다.",
            event.beforeRole() + " -> " + event.afterRole(),
            event.userId()
        )
    );
  }

}
