package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    @Query("select rs from ReadStatus rs where rs.channel.id = :channelId")
    List<ReadStatus> findAllByChannelId(UUID channelId);

    @Query("""
            select (count(rs) > 0)
            from ReadStatus rs
            where rs.user.id = :userId
              and rs.channel.id = :channelId
            """)
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    @Query("select rs from ReadStatus rs where rs.user.id = :userId")
    List<ReadStatus> findAllByUserId(UUID userId);
}
