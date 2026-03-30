package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelId);

    @Query("""
SELECT rs
FROM ReadStatus rs
JOIN FETCH rs.user
WHERE rs.channel.id IN :channelIds
""")
    List<ReadStatus> findAllByChannelIdIn(List<UUID> channelIds);
}
