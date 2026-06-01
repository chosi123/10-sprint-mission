package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;

  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT
  )
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
    binaryContentStorage.put(event.binaryContentId(), event.bytes(), event.fileName(), event.contentType());
  }

}
