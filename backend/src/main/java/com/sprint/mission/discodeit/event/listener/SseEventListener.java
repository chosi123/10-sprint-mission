package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.BinaryContentStatusUpdatedEvent;
import com.sprint.mission.discodeit.event.ChannelEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.UserEvent;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SseEventListener {

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async("eventExecutor")
  public void handleNotificationCreated(NotificationCreatedEvent event) {
    sseService.send(
        List.of(event.receiverId()),
        "notifications.created",
        event.notification()
    );
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async("eventExecutor")
  public void handleBinaryContentStatusUpdated(BinaryContentStatusUpdatedEvent event) {
    sseService.broadcast("binaryContents.updated", event.binaryContent());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async("eventExecutor")
  public void handleChannelEvent(ChannelEvent event) {
    if (event.channel().type() == ChannelType.PRIVATE) {
      List<UUID> participantIds = event.channel().participants().stream()
          .map(UserDto::id)
          .toList();
      sseService.send(participantIds, event.eventName(), event.channel());
    } else {
      sseService.broadcast(event.eventName(), event.channel());
    }
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async("eventExecutor")
  public void handleUserEvent(UserEvent event) {
    sseService.broadcast(event.eventName(), event.user());
  }
}
