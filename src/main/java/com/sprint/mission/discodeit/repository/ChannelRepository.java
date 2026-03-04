package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    @Query("SELECT c FROM Channel c WHERE c.type = com.sprint.mission.discodeit.entity.ChannelType.PUBLIC " +
            "OR c IN (SELECT r.channel FROM ReadStatus r WHERE r.user.id = :userId AND c.type = com.sprint.mission.discodeit.entity.ChannelType.PRIVATE)")
    List<Channel> findAllAccessibleChannelsByUserId(UUID userId);
}
