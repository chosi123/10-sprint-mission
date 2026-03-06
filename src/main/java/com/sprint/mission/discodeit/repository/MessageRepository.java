package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByChannelIdOrderByCreatedAtAsc(UUID channelId);
    @EntityGraph(attributePaths = {"author", "author.userStatus"})
    Slice<Message> findByChannelId(UUID channelId, Pageable pageable);
    @Query("SELECT max(m.createdAt) FROM Message m WHERE m.channel.id= :channelId")
    Instant findLastMessageTimeWithChannelId(UUID channelId);


}
