package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Slice<Message> findByChannel_IdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    Slice<Message> findByChannel_IdAndCreatedAtLessThanOrderByCreatedAtDesc(
            UUID channelId,
            Instant cursor,
            Pageable pageable
    );

    @Query("select max(m.createdAt) from Message m where m.channel.id = :channelId")
    Instant findLastMessageAtByChannelId(UUID channelId);
}
